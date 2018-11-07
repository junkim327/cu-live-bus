package com.example.junyoung.uiucbus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyRecyclerView;
import com.example.junyoung.uiucbus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.util.listener.RecyclerviewClickListener;
import com.example.junyoung.uiucbus.adapter.BusFavoriteDeparturesAdapter;
import com.example.junyoung.uiucbus.adapter.MainViewPagerAdapter;
import com.example.junyoung.uiucbus.httpclient.pojos.FavoriteBusDepartures;
import com.example.junyoung.uiucbus.ui.Injection;
import com.example.junyoung.uiucbus.ui.factory.UserSavedBusStopViewModelFactory;
import com.example.junyoung.uiucbus.ui.viewmodel.BusDeparturesViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.Response;
import com.example.junyoung.uiucbus.ui.viewmodel.SavedBusStopViewModel;
import com.example.junyoung.uiucbus.util.UtilConnection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DashboardFragment extends Fragment {
  private static final String TAG = DashboardFragment.class.getSimpleName();

  private String mUid;
  private int mNumUserSavedBusStops;
  private boolean isInternetConnected = true;

  private Unbinder mUnbinder;
  private ConnectivityManager mConnectivityManager;
  private BusFavoriteDeparturesAdapter mAdapter;
  private BusDeparturesViewModel mBusDepartureViewModel;
  private SavedBusStopViewModel mViewModel;
  private UserSavedBusStopViewModelFactory mViewModelFactory;
  private OnInternetConnectedListener mInternetConnectedCallback;
  private OnSettingItemSelectedListener mSettingSelectedCallback;
  private OnBusStopsSearchviewClickedListener mBusStopsSearchviewClickedCallback;
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  @BindView(R.id.toolbar_dashboard)
  Toolbar mToolbar;
  @BindView(R.id.edittext_search_bus_stops_dashboard)
  EditText mSearchBusStopsEditText;
  @BindView(R.id.progressbar_dashboard)
  ProgressBar mProgressBar;
  @BindView(R.id.viewpager_dashboard)
  ViewPager mViewPager;
  @BindView(R.id.recyclerview_dashboard)
  EpoxyRecyclerView mRecyclerview;
  @BindView(R.id.text_data_provider_dashboard)
  TextView mDataProviderText;
  @BindView(R.id.image_when_no_favorite_bus_stops_dashboard)
  ImageView mGuidanceImage;
  @BindView(R.id.text_guidance_message_one_dashboard)
  TextView mGuidanceTextOne;
  @BindView(R.id.text_guidance_message_two_dashboard)
  TextView mGuidanceTextTwo;

  public interface OnSettingItemSelectedListener {
    void onSettingItemSelected();
  }

  public interface OnBusStopsSearchviewClickedListener {
    void onBusStopsSearchviewClicked();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    Log.d(TAG, "onAttach has called");

    try {
      mInternetConnectedCallback = (OnInternetConnectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnInternetConnectedListener.");
    }

    try {
      mSettingSelectedCallback = (OnSettingItemSelectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnSettingItemSelectedListener.");
    }

    try {
      mBusStopsSearchviewClickedCallback = (OnBusStopsSearchviewClickedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnBusStopsSearchviewClickedListener.");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate has called.");

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );

    mUid = sharedPref.getString(getString(R.string.saved_uid), null);
    // mNumUserSavedBusStops = sharedPref.getInt(getString(R.string.saved_bus_stops), 0);

    mViewModelFactory = Injection.provideUserSavedBusStopViewModelFactory(getContext());
    mViewModel = ViewModelProviders.of(this, mViewModelFactory)
      .get(SavedBusStopViewModel.class);
  }

  private void setToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      setHasOptionsMenu(true);
      actionBar.setDisplayShowTitleEnabled(false);
    }
  }

  private void setViewPager() {
    mViewPager.setClipToPadding(false);
    mViewPager.setPageMargin(24);
    mViewPager.setAdapter(new MainViewPagerAdapter(getContext()));
  }

  private void setRecyclerView() {
    mRecyclerview.setHasFixedSize(true);
    mRecyclerview.setNestedScrollingEnabled(false);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerview.setLayoutManager(layoutManager);
    ((SimpleItemAnimator) mRecyclerview.getItemAnimator())
      .setSupportsChangeAnimations(false);

    RecyclerviewClickListener departureClickListener = new RecyclerviewClickListener() {
      @Override
      public void onClick(View view, int position) {

      }
    };
    mAdapter = new BusFavoriteDeparturesAdapter(getContext(), departureClickListener);
    mRecyclerview.setAdapter(mAdapter);
  }

  private void controlViewVisibility() {
    if (mNumUserSavedBusStops != 0) {
      mRecyclerview.setVisibility(VISIBLE);
      mDataProviderText.setVisibility(VISIBLE);
      mGuidanceImage.setVisibility(GONE);
      mGuidanceTextOne.setVisibility(GONE);
      mGuidanceTextTwo.setVisibility(GONE);
    } else {
      mRecyclerview.setVisibility(GONE);
      mDataProviderText.setVisibility(GONE);
      mGuidanceImage.setVisibility(VISIBLE);
      mGuidanceTextOne.setVisibility(VISIBLE);
      mGuidanceTextTwo.setVisibility(VISIBLE);
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (mConnectivityManager != null) {
      isInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, false);
    }
    Log.d(TAG, "onCreateView has called");

    View view = null;
    if (isInternetConnected) {
      view = inflater.inflate(R.layout.fragment_dashboard, container, false);
      mUnbinder = ButterKnife.bind(this, view);

      setToolbar();
      setViewPager();
      setRecyclerView();
    }

    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Log.d(TAG, "onActivityCreated has called");

    if (mBusDepartureViewModel == null) {
      mBusDepartureViewModel = ViewModelProviders.of(this).get(BusDeparturesViewModel.class);
      mBusDepartureViewModel.getResponse().observe(this, listResponse -> {
        Log.d(TAG, "getResponse() has called.");
        if (listResponse != null) {
          Log.d(TAG, "Status : " + listResponse.mStatus.toString());
          processResponse(listResponse);
        }
      });
    }
  }

  private void processResponse(Response<List<FavoriteBusDepartures>> response) {
    switch (response.mStatus) {
      case LOADING:
        mProgressBar.setVisibility(VISIBLE);
        break;

      case SUCCESS:
        mProgressBar.setVisibility(GONE);
        if (response.mData != null) {
          mAdapter.updateBusDepartures(response.mData);
        }
        break;

      case ERROR:
        mProgressBar.setVisibility(GONE);
        response.mError.printStackTrace();
        break;
    }
  }

  @OnClick(R.id.edittext_search_bus_stops_dashboard)
  public void startBusStopSearchFragment() {
    mBusStopsSearchviewClickedCallback.onBusStopsSearchviewClicked();
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.d(TAG, "onStart has called.");

    if (mUid != null) {
      mDisposable.add(mViewModel.loadAllBusStopsByUid(mUid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(userSavedBusStops -> {
            if (userSavedBusStops != null) {
              Log.d(TAG, "Load user saved bus stops successfully.");
              Log.d(TAG, "bus stops: " + userSavedBusStops.size());
              mNumUserSavedBusStops = userSavedBusStops.size();
              controlViewVisibility();

              mBusDepartureViewModel.loadUserFavoriteDepartures(userSavedBusStops);
            }
          },
          throwable -> Log.e(TAG, "Unable to load user saved bus stops", throwable)));
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume has called.");

    if (mConnectivityManager != null) {
      UtilConnection.isInternetConnected(mConnectivityManager, mInternetConnectedCallback, false);
    }
  }

  @Override
  public void onStop() {
    super.onStop();

    // Log.d(TAG, "Item count : " + mAdapter.getItemCount());
    mDisposable.clear();
    mRecyclerview.onDetachedFromWindow();
    mBusDepartureViewModel.dispose();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.setting_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_setting:
        mSettingSelectedCallback.onSettingItemSelected();

        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }
}
