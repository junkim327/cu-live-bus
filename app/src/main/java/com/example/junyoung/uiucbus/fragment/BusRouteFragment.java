package com.example.junyoung.uiucbus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.TextView;

import com.example.junyoung.uiucbus.OnHomeItemClickedListener;
import com.example.junyoung.uiucbus.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.adapter.BusRouteAdapter;
import com.example.junyoung.uiucbus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.uiucbus.ui.viewmodel.BusRouteViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedDepartureViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedStopPointViewModel;
import com.example.junyoung.uiucbus.utils.UtilConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BusRouteFragment extends Fragment {
  private static final String TAG = BusRouteFragment.class.getSimpleName();

  private boolean mIsInternetConnected;

  private MenuItem mMapMenuItem;

  private Unbinder mUnbinder;
  private BusRouteAdapter mAdapter;
  private ConnectivityManager mConnectivityManager;

  // ViewModels
  private BusRouteViewModel mBusRouteViewModel;
  private SharedStopPointViewModel mSharedStopPointViewModel;
  private SharedDepartureViewModel mSharedDepartureViewModel;

  // Callbacks
  private OnMapItemClickedListener mMapItemClickedCallback;
  private OnHomeItemClickedListener mHomeItemClickedCallback;
  private OnInternetConnectedListener mInternetConnectedCallback;

  @BindView(R.id.toolbar_bus_routes)
  Toolbar mToolbar;
  @BindView(R.id.appbar_layout_bus_routes)
  AppBarLayout mAppBarLayout;
  @BindView(R.id.collapsing_toolbar_layout_bus_routes)
  CollapsingToolbarLayout mCollapsingToolbarLayout;
  @BindView(R.id.textview_bus_routes)
  TextView mBusRouteTextView;
  @BindView(R.id.textview_bus_name_bus_routes)
  TextView mBusNameTextView;
  @BindView(R.id.recycler_view_bus_routes)
  RecyclerView mRecyclerView;

  public interface OnMapItemClickedListener {
    void onMapItemClicked();
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
      mHomeItemClickedCallback = (OnHomeItemClickedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnHomeItemClickedListener.");
    }

    try {
      mMapItemClickedCallback = (OnMapItemClickedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnMapItemClickedListener.");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "onCreate has called");

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    mIsInternetConnected = true;
    if (mConnectivityManager != null) {
      mIsInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, false);
    }

    Log.d(TAG, "onCreateView has called");

    View view = null;
    if (mIsInternetConnected) {
      view = inflater.inflate(R.layout.activity_bus_routes, container, false);
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

  private void setRecyclerView() {
    mRecyclerView.setHasFixedSize(true);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerView.setLayoutManager(layoutManager);

    mAdapter = new BusRouteAdapter(getContext());
    mRecyclerView.setAdapter(mAdapter);
  }

  private void setAppBarLayout(String busName, String busRoute) {
    mBusNameTextView.setText(busName);
    mBusRouteTextView.setText(busRoute);

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
          if (mMapMenuItem != null) {
            mMapMenuItem.setVisible(true);
          }
          mCollapsingToolbarLayout.setTitle(busName);
          mCollapsingToolbarLayout.setCollapsedTitleTextColor(
            getResources().getColor(R.color.white)
          );
          isShow = true;
        } else if (isShow) {
          // TODO: store empty string to string resource
          mCollapsingToolbarLayout.setTitle(" ");
          if (mMapMenuItem != null) {
            mMapMenuItem.setVisible(false);
          }
          isShow = false;
        }
      }
    });
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Log.d(TAG, "onActivityCreated has called");

    mBusRouteViewModel = ViewModelProviders.of(this).get(BusRouteViewModel.class);

    mSharedStopPointViewModel = ViewModelProviders.of(getActivity())
      .get(SharedStopPointViewModel.class);

    mSharedDepartureViewModel = ViewModelProviders.of(getActivity())
      .get(SharedDepartureViewModel.class);
    SortedDeparture departure = mSharedDepartureViewModel.getDeparture().getValue();
    if (departure != null) {
      setAppBarLayout(departure.getHeadSign(), departure.getVehicleIdList().get(0));
      mBusRouteViewModel.init(departure.getTripList().get(0).getTripId());
    }

    mBusRouteViewModel.getRouteList().observe(this, routeList -> {
      if (routeList != null && departure != null) {
        String busStopName = getResources().getString(R.string.empty_string);
        if (mSharedStopPointViewModel.getSelectedStopPoint().getValue() != null) {
          busStopName = mSharedStopPointViewModel.getSelectedStopPoint().getValue().getStopName();
        }
        mAdapter.updateStopTimesList(
          departure.getRouteList().get(0).getRouteColor(),
          busStopName,
          routeList
        );

        int pos = 0;
        for (int i = 0; i < routeList.size(); i++) {
          if (routeList.get(i).getStopPoint().getStopName().contentEquals(busStopName)) {
            pos = i;
          }
        }
        mRecyclerView.scrollToPosition(pos);
        mAppBarLayout.setExpanded(false);
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume has called");
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.bus_route_menu, menu);
    if (menu != null) {
      mMapMenuItem = menu.findItem(R.id.action_map_bus_route);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        return true;
      case R.id.action_map_bus_route:
        mMapItemClickedCallback.onMapItemClicked();
        return true;
      case R.id.action_home_bus_route:
        mHomeItemClickedCallback.onHomeItemClicked();
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
