package com.example.junyoung.uiucbus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.junyoung.uiucbus.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.RecyclerviewClickListener;
import com.example.junyoung.uiucbus.adapter.BusDeparturesAdapter;
import com.example.junyoung.uiucbus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.uiucbus.room.entity.StopPoint;
import com.example.junyoung.uiucbus.ui.Injection;
import com.example.junyoung.uiucbus.ui.factory.UserSavedBusStopViewModelFactory;
import com.example.junyoung.uiucbus.ui.viewmodel.BusDeparturesViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedDepartureViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedStopPointViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.UserSavedBusStopViewModel;
import com.example.junyoung.uiucbus.utils.UtilConnection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.View.GONE;

public class BusDeparturesFragment extends Fragment {
  private static final int MAX_NUM_SAVED_STOPS = 3;
  private static final String TAG = BusDeparturesFragment.class.getSimpleName();

  private int mSize = 0;
  private String mUid;

  private Unbinder mUnbinder;
  private BusDeparturesAdapter mAdapter;
  private ConnectivityManager mConnectivityManager;
  private OnHomeItemClickedListener mHomeItemClickedCallback;
  private OnDepartureClickedListener mDepartureClickedCallback;
  private OnInternetConnectedListener mInternetConnectedCallback;
  private BusDeparturesViewModel mBusDeparturesViewModel;
  private SharedDepartureViewModel mSharedDepartureViewModel;
  private SharedStopPointViewModel mSharedStopPointViewModel;
  private UserSavedBusStopViewModel mUserSavedBusStopViewModel;
  private UserSavedBusStopViewModelFactory mUserSavedBusStopViewModelFactory;

  @BindView(R.id.toolbar_bus_departures)
  Toolbar mToolbar;
  @BindView(R.id.appbar_layout_bus_departures)
  AppBarLayout mAppBarLayout;
  @BindView(R.id.collapsing_toolbar_layout_bus_departures)
  CollapsingToolbarLayout mCollapsingToolbarLayout;
  @BindView(R.id.textview_bus_stop_name_bus_departures)
  TextView mBusStopNameTextView;
  @BindView(R.id.textview_bus_stop_code_bus_departures)
  TextView mBusStopCodeTextView;
  @BindView(R.id.progressbar_bus_departures)
  ProgressBar mProgressBar;
  @BindView(R.id.recycler_view_bus_departures)
  RecyclerView mRecyclerView;
  @BindView(R.id.floating_action_button_bus_departures)
  FloatingActionButton floatingActionButton;
  @BindView(R.id.toggle_button_bus_departures)
  ToggleButton addFavoriteButton;
  @BindView(R.id.image_unselected_button_bus_departures)
  ImageView unselectedButtonImageView;

  public interface OnHomeItemClickedListener {
    void onHomeItemClicked();
  }

  public interface OnDepartureClickedListener {
    void onDepartureClicked();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );

    try {
      mDepartureClickedCallback = (OnDepartureClickedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnDepartureClickedListener.");
    }

    try {
      mInternetConnectedCallback = (OnInternetConnectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnInternetConnectedListener.");
    }

    try {
      mHomeItemClickedCallback = (OnHomeItemClickedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnHomeItemClickedListener.");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    mUid = sharedPref.getString(getString(R.string.saved_uid), null);

    mUserSavedBusStopViewModelFactory =
      Injection.provideUserSavedBusStopViewModelFactory(getContext());
    mUserSavedBusStopViewModel = ViewModelProviders.of(this, mUserSavedBusStopViewModelFactory)
      .get(UserSavedBusStopViewModel.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    boolean isInternetConnected = true;
    if (mConnectivityManager != null) {
      isInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, false);
    }

    View view = null;
    if (isInternetConnected) {
      view = inflater.inflate(R.layout.activity_bus_departures, container, false);
      mUnbinder = ButterKnife.bind(this, view);

      setToolbar();
      setRecyclerView();
    }

    return view;
  }

  private void setToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      setHasOptionsMenu(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }
  }

  private void setAppBarLayout(String busStopName, String busStopCode) {
    mBusStopNameTextView.setText(busStopName);
    mBusStopCodeTextView.setText(busStopCode);

    mCollapsingToolbarLayout.setTitleEnabled(true);

    mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      boolean isShow = true;
      int scrollRange = -1;

      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
          isShow = true;
          mCollapsingToolbarLayout.setTitle(busStopCode);
          mCollapsingToolbarLayout.setCollapsedTitleTextColor(
            getResources().getColor(R.color.white)
          );
        } else if (isShow) {
          isShow = false;
          mCollapsingToolbarLayout.setTitle(" ");
        }
      }
    });
  }

  private void setRecyclerView() {
    mRecyclerView.setHasFixedSize(true);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerView.setLayoutManager(layoutManager);

    RecyclerviewClickListener listener = (view, position) -> {
      List<SortedDeparture> sortedDepartureList =
        mBusDeparturesViewModel.getSortedDepartureList().getValue();
      if (sortedDepartureList != null) {
        mSharedDepartureViewModel.select(sortedDepartureList.get(position));
        mDepartureClickedCallback.onDepartureClicked();
      }

    };
    mAdapter = new BusDeparturesAdapter(getContext(), listener);
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mSharedDepartureViewModel = ViewModelProviders.of(getActivity())
      .get(SharedDepartureViewModel.class);

    mSharedStopPointViewModel = ViewModelProviders.of(getActivity())
      .get(SharedStopPointViewModel.class);
    StopPoint stopPoint = mSharedStopPointViewModel.getSelectedStopPoint().getValue();
    if (stopPoint != null) {
      setAppBarLayout(stopPoint.getStopName(), stopPoint.getStopCode());
    }

    mBusDeparturesViewModel = ViewModelProviders.of(getActivity()).get(BusDeparturesViewModel.class);
    mBusDeparturesViewModel.init(stopPoint.getStopId());
    mBusDeparturesViewModel.getSortedDepartureList().observe(this, sortedDepartureList -> {
      if (sortedDepartureList != null) {
        Log.d(TAG, "Size: " + sortedDepartureList.size());
        mSize = sortedDepartureList.size();
        mAdapter.updateDepartureList(sortedDepartureList);
      }
    });
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.d(TAG, "onPause has called");
    for (int i = 0; i < mSize; i++) {
      mAdapter.cancelTimer(mRecyclerView.findViewHolderForAdapterPosition(i));
    }
    if (mAdapter.getViewHolder() != null) {
      //mAdapter.onViewDetachedFromWindow(mAdapter.getViewHolder());
    }
    mBusDeparturesViewModel.cancelTimer();
  }

  @Override
  public void onStop() {
    super.onStop();
    Log.d(TAG, "onStop has called");
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.home_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        if (getFragmentManager() != null) {
          getFragmentManager().popBackStackImmediate();
        }
        return true;
      case R.id.action_home:
        mHomeItemClickedCallback.onHomeItemClicked();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
