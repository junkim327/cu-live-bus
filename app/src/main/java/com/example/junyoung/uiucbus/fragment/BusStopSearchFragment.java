package com.example.junyoung.uiucbus.fragment;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junyoung.uiucbus.Constants;
import com.example.junyoung.uiucbus.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.RecyclerviewClickListener;
import com.example.junyoung.uiucbus.adapter.BusStopsAdapter;
import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.pojos.BusStops;
import com.example.junyoung.uiucbus.httpclient.pojos.Stop;
import com.example.junyoung.uiucbus.httpclient.services.BusStopsServices;
import com.example.junyoung.uiucbus.room.entity.StopPoint;
import com.example.junyoung.uiucbus.ui.Injection;
import com.example.junyoung.uiucbus.ui.factory.UserSearchedBusStopViewModelFactory;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedStopPointViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.UserSearchedBusStopViewModel;
import com.example.junyoung.uiucbus.utils.UtilConnection;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusStopSearchFragment extends Fragment {
  private static final String TAG = BusStopSearchFragment.class.getSimpleName();

  private String mUid;
  private boolean mIsInternetConnected = true;

  private ArrayList<StopPoint> mStopPointList = new ArrayList<>();
  private ArrayList<StopPoint> mUserSearchedBusStopPointList = new ArrayList<>();

  private BusStopsAdapter mAdapter;
  private Unbinder mUnbinder;
  private SearchView mSearchView;
  private ConnectivityManager mConnectivityManager;
  private SharedStopPointViewModel mSharedStopPointViewModel;
  private UserSearchedBusStopViewModel mViewModel;
  private UserSearchedBusStopViewModelFactory mViewModelFactory;
  private OnBusStopSelectedListener mBusStopSelectedCallback;
  private OnInternetConnectedListener mInternetConnectedCallback;
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  @BindView(R.id.toolbar_bus_stop_search)
  Toolbar mToolbar;
  @BindView(R.id.recyclerview_bus_stop_search)
  RecyclerView mRecyclerView;
  @BindView(R.id.image_warning_sign_bus_stop_search)
  ImageView mWarningImageView;
  @BindView(R.id.text_no_result_bus_stop_search)
  TextView mNoResultTextView;

  public interface OnBusStopSelectedListener {
    void onBusStopSelected();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    try {
      mInternetConnectedCallback = (OnInternetConnectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnInternetConnectedListener.");
    }

    try {
      mBusStopSelectedCallback = (OnBusStopSelectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnBusStopSelectedListener.");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    mUid = sharedPref.getString(getString(R.string.saved_uid), null);
  }

  private void setToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      setHasOptionsMenu(true);
      actionBar.setDisplayShowTitleEnabled(false);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black);
    }
  }

  private void setRecyclerView() {
    mRecyclerView.setHasFixedSize(true);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerView.setLayoutManager(layoutManager);

    RecyclerviewClickListener listener = (view, position) -> {
      if (mStopPointList != null) {
        StopPoint stopPoint = mStopPointList.get(position);
        mSharedStopPointViewModel.select(stopPoint);
        mDisposable.add(mViewModel.insertBusStop(mUid, stopPoint.getStopId(),
          stopPoint.getStopCode(), stopPoint.getStopName(), stopPoint.getLatitude(),
          stopPoint.getLongitude())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> Log.i(TAG, "Stop point is successfully stored in the database."),
          throwable -> Log.e(TAG, "Unable to insert place :(", throwable)));
        mBusStopSelectedCallback.onBusStopSelected();
      }
    };
    mAdapter = new BusStopsAdapter(getContext(), listener);
    mRecyclerView.setAdapter(mAdapter);

    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
      mRecyclerView.getContext(),
      DividerItemDecoration.VERTICAL
    );
    mRecyclerView.addItemDecoration(dividerItemDecoration);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (mConnectivityManager != null) {
      mIsInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, false);
    }

    View view = null;
    if (mIsInternetConnected) {
      view = inflater.inflate(R.layout.fragment_bus_stop_search, container, false);
      mUnbinder = ButterKnife.bind(this, view);

      setToolbar();
      setRecyclerView();
    }

    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mViewModelFactory = Injection.provideUserSearchedBusStopViewModelFactory(getContext());
    mViewModel = ViewModelProviders.of(this, mViewModelFactory)
      .get(UserSearchedBusStopViewModel.class);

    mSharedStopPointViewModel = ViewModelProviders.of(getActivity())
      .get(SharedStopPointViewModel.class);
  }

  @Override
  public void onStart() {
    super.onStart();

    if (mUid != null && mIsInternetConnected) {
      mDisposable.add(mViewModel.loadAllBusStopsByUid(mUid)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(userSearchedBusStops -> {
        if (userSearchedBusStops != null) {
          mAdapter.updateStopsList(userSearchedBusStops);
        }
      },
        throwable -> Log.e(TAG, "Unable to load user saved bus stops", throwable)));
    }
  }

  private void setSearchEditText() {
    EditText searchEditText = mSearchView.findViewById(
      android.support.v7.appcompat.R.id.search_src_text
    );

    searchEditText.setLinkTextColor(ResourcesCompat.getColor(getResources(),
      R.color.colorPrimaryDark, null));
    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.oxygen);
    searchEditText.setTypeface(typeface);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.options_menu, menu);

    SearchManager searchManager =
      (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
    mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    mSearchView.setSearchableInfo(
      searchManager.getSearchableInfo(getActivity().getComponentName())
    );

    setSearchEditText();

    mSearchView.setIconified(false);
    mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
    mSearchView.setMaxWidth(Integer.MAX_VALUE);
    mSearchView.requestFocus();

    ImageView searchIcon =
      mSearchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
    searchIcon.setImageDrawable(null);

    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        if (mStopPointList == null || mStopPointList.isEmpty()) {
          mRecyclerView.setVisibility(View.INVISIBLE);
          mWarningImageView.setVisibility(View.VISIBLE);
          mNoResultTextView.setVisibility(View.VISIBLE);
        }

        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mWarningImageView.setVisibility(View.INVISIBLE);
        mNoResultTextView.setVisibility(View.INVISIBLE);

        BusStopsServices service =
          RetrofitBuilder.getRetrofitInstance().create(BusStopsServices.class);
        Call<BusStops> call = service.getStopsBySearch(Constants.API_KEY, newText);

        call.enqueue(new Callback<BusStops>() {
          @Override
          public void onResponse(Call<BusStops> call, Response<BusStops> response) {
            if (response.isSuccessful()) {
              ArrayList<Stop> stops = response.body().getStops();

              if (stops != null && !stops.isEmpty()) {
                mStopPointList = response.body().getStops().get(0).getStopPoints();
              }
              mAdapter.updateStopsList(mStopPointList);
            }
          }

          @Override
          public void onFailure(Call<BusStops> call, Throwable t) {

          }
        });

        return false;
      }
    });

    final ImageView closeButton = mSearchView.findViewById(R.id.search_close_btn);
    closeButton.setVisibility(View.GONE);
    closeButton.setOnClickListener(view -> {
      mStopPointList.clear();
      mAdapter.notifyItemRangeChanged(0, mStopPointList.size());
      mSearchView.setQuery("", false);
      closeButton.setVisibility(View.GONE);
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    mUnbinder.unbind();
  }
}
