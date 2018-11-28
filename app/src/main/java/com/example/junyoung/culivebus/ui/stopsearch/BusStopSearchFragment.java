package com.example.junyoung.culivebus.ui.stopsearch;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junyoung.culivebus.httpclient.pojos.Stop;
import com.example.junyoung.culivebus.ui.viewmodel.BusStopViewModel;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;
import com.example.junyoung.culivebus.adapter.BusStopsAdapter;
import com.example.junyoung.culivebus.room.entity.StopPoint;
import com.example.junyoung.culivebus.ui.Injection;
import com.example.junyoung.culivebus.ui.factory.UserSearchedBusStopViewModelFactory;
import com.example.junyoung.culivebus.ui.viewmodel.SharedStopPointViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SearchedBusStopViewModel;
import com.example.junyoung.culivebus.util.UtilConnection;
import com.example.junyoung.culivebus.vo.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class BusStopSearchFragment extends Fragment {
  private static final String TAG = BusStopSearchFragment.class.getSimpleName();

  private String mUid;
  private int mClickTag;
  private boolean mIsInternetConnected = true;

  private List<StopPoint> mStopPointList = new ArrayList<>();
  private List<StopPoint> mStopPointFromDBList = new ArrayList<>();

  private BusStopsAdapter mAdapter;
  private Unbinder mUnbinder;
  private SearchView mSearchView;
  private ConnectivityManager mConnectivityManager;
  private OnBusStopSelectedListener mBusStopSelectedCallback;
  private OnInternetConnectedListener mInternetConnectedCallback;
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  // ViewModels
  private SearchedBusStopViewModel mViewModel;
  private BusStopViewModel mBusStopViewModel;
  private UserSearchedBusStopViewModelFactory mViewModelFactory;
  private SharedStopPointViewModel mSharedStopPointViewModel;

  @BindView(R.id.toolbar_bus_stop_search)
  Toolbar mToolbar;
  @BindView(R.id.text_header_bus_stop_search)
  TextView mHeaderTextView;
  @BindView(R.id.recyclerview_bus_stop_search)
  RecyclerView mRecyclerView;
  @BindView(R.id.group_no_result_bus_stop_search)
  Group mNoResultGroup;

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

  private void setToolbar() {
    ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mToolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      setHasOptionsMenu(true);
      actionBar.setDisplayShowTitleEnabled(false);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }
  }

  private void setRecyclerView() {
    mRecyclerView.setHasFixedSize(true);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerView.setLayoutManager(layoutManager);

    RecyclerviewClickListener listener = this::onRecyclerViewItemClicked;
    mAdapter = new BusStopsAdapter(getContext(), listener);
    mRecyclerView.setAdapter(mAdapter);

    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
      mRecyclerView.getContext(),
      DividerItemDecoration.VERTICAL
    );
    mRecyclerView.addItemDecoration(dividerItemDecoration);
  }

  /**
   * TODO: later
   *
   * @param view ItemView which user clicked.
   * @param position Adapter position of the ItemView.
   */
  private void onRecyclerViewItemClicked(View view, int position) {
    StopPoint stopPoint;
    if (mStopPointList != null && mClickTag == 0) {
      stopPoint = mStopPointList.get(position);
      mSharedStopPointViewModel.select(stopPoint);
      mDisposable.add(mViewModel.insertBusStop(mUid, stopPoint.getStopId(),
        stopPoint.getStopCode(), stopPoint.getStopName(), stopPoint.getLatitude(),
        stopPoint.getLongitude())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> Log.i(TAG, "Stop point is successfully stored in the database."),
          throwable -> Log.e(TAG, "Unable to insert place :(", throwable)));
    } else {
      stopPoint = mStopPointFromDBList.get(position);
      mSharedStopPointViewModel.select(stopPoint);
    }
    mBusStopSelectedCallback.onBusStopSelected();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mViewModelFactory = Injection.provideUserSearchedBusStopViewModelFactory(getContext());

    mBusStopViewModel = ViewModelProviders.of(this).get(BusStopViewModel.class);
    mBusStopViewModel.getBusStopList().observe(this, response -> {
      if (response != null) {
        processResponse(response);
      }
    });

    mSharedStopPointViewModel = ViewModelProviders.of(getActivity())
      .get(SharedStopPointViewModel.class);
  }

  private void processResponse(Response<List<Stop>> response) {
    switch (response.mStatus) {
      case SUCCESS:
        if (response.mData != null && !response.mData.isEmpty()) {
          mHeaderTextView.setVisibility(GONE);
          mClickTag = 0;
          mStopPointList = response.mData.get(0).getStopPoints();
          mAdapter.updateStopsList(mStopPointList);
        }
        break;
      case ERROR:
        response.mError.printStackTrace();
        break;
    }
  }

  @Override
  public void onStart() {
    super.onStart();

    mViewModel = ViewModelProviders.of(this, mViewModelFactory)
      .get(SearchedBusStopViewModel.class);

    if (mUid != null && mIsInternetConnected) {
      loadUserSearchedBusStopList();
    }
  }

  private void loadUserSearchedBusStopList() {
    mDisposable.add(mViewModel.loadAllBusStopsByUid(mUid)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(userSearchedBusStops -> {
          if (userSearchedBusStops != null) {
            if (userSearchedBusStops.size() == 0) {
              mHeaderTextView.setVisibility(GONE);
            } else {
              mHeaderTextView.setVisibility(VISIBLE);
            }

            mClickTag = 1;
            mStopPointFromDBList.clear();
            mStopPointFromDBList.addAll(userSearchedBusStops);
            mAdapter.updateStopsList(userSearchedBusStops);
          }
        },
        throwable -> Log.e(TAG, "Unable to load user saved bus stops", throwable)));
  }

  @Override
  public void onStop() {
    super.onStop();
    mDisposable.clear();
  }

  private void setupSearchEditText() {
    EditText searchEditText = mSearchView.findViewById(
      android.support.v7.appcompat.R.id.search_src_text
    );

    searchEditText.setTextColor(getResources().getColor(R.color.white));
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.options_menu, menu);

    //ImageView searchIcon =
      //mSearchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
    //searchIcon.setImageDrawable(null);

    setupSearchView(menu);
  }

  private void setupSearchView(Menu menu) {
    SearchManager searchManager =
      (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
    mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    mSearchView.setSearchableInfo(
      Objects.requireNonNull(searchManager).getSearchableInfo(getActivity().getComponentName())
    );

    mSearchView.setIconified(false);
    mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
    mSearchView.setMaxWidth(Integer.MAX_VALUE);
    mSearchView.requestFocus();

    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit has called.");
        if (mStopPointList == null || mStopPointList.isEmpty()) {
          Log.d(TAG, "mStopPointList is empty.");
          mHeaderTextView.setVisibility(GONE);
          mRecyclerView.setVisibility(INVISIBLE);
          mNoResultGroup.setVisibility(VISIBLE);
        }

        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        if (newText.contentEquals("")) {
          Log.d(TAG, "Empty String");
        }

        mRecyclerView.setVisibility(VISIBLE);
        mNoResultGroup.setVisibility(GONE);
        //mNoResultGroup.setVisibility(INVISIBLE);

        if (newText.contentEquals("")) {
          mHeaderTextView.setVisibility(VISIBLE);
          mAdapter.updateStopsList(mStopPointFromDBList);
          return false;
        } else {
          mBusStopViewModel.requestSearchResult(newText);
        }

        return false;
      }
    });

    setupSearchEditText();
    setupCloseButton();
  }

  private void setupCloseButton() {
    final ImageView closeButton = mSearchView.findViewById(R.id.search_close_btn);
    closeButton.setColorFilter(getResources().getColor(R.color.white));
    closeButton.setVisibility(View.GONE);
    closeButton.setOnClickListener(view -> {
      mSearchView.setQuery("", false);
      closeButton.setVisibility(View.GONE);
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      FragmentManager fm = getFragmentManager();
      if (fm != null && fm.getBackStackEntryCount() > 0) {
        fm.popBackStack();
      }
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    if (mUnbinder != null) {
      mUnbinder.unbind();
      mUnbinder = null;
    }
  }
}
