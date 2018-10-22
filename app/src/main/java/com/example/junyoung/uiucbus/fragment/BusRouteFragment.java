package com.example.junyoung.uiucbus.fragment;

import android.arch.lifecycle.Observer;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.uiucbus.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.adapter.BusRoutesAdapter;
import com.example.junyoung.uiucbus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.uiucbus.httpclient.pojos.StopTimes;
import com.example.junyoung.uiucbus.ui.viewmodel.BusRouteViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedDepartureViewModel;
import com.example.junyoung.uiucbus.utils.UtilConnection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BusRouteFragment extends Fragment {
  private boolean mIsInternetConnected;

  private Unbinder mUnbinder;
  private BusRoutesAdapter mAdapter;
  private ConnectivityManager mConnectivityManager;
  private BusRouteViewModel mBusRouteViewModel;
  private SharedDepartureViewModel mSharedDepartureViewModel;
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

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    try {
      mInternetConnectedCallback = (OnInternetConnectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnInternetConnectedListener.");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

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

    View view = null;
    if (mIsInternetConnected) {
      view = inflater.inflate(R.layout.activity_bus_routes, container, false);
      mUnbinder = ButterKnife.bind(this, view);
    }

    return view;
  }

  private void setToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }
  }

  private void setRecyclerView() {
    mRecyclerView.setHasFixedSize(true);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerView.setLayoutManager(layoutManager);

    mAdapter = new BusRoutesAdapter(getContext());
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
          mCollapsingToolbarLayout.setTitle(busName);
          mCollapsingToolbarLayout.setCollapsedTitleTextColor(
            getResources().getColor(R.color.white)
          );
          isShow = true;
        } else if (isShow) {
          // TODO: store empty string to string resource
          mCollapsingToolbarLayout.setTitle(" ");
          isShow = false;
        }
      }
    });
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mBusRouteViewModel = ViewModelProviders.of(this).get(BusRouteViewModel.class);
    mSharedDepartureViewModel = ViewModelProviders.of(getActivity())
      .get(SharedDepartureViewModel.class);
    SortedDeparture departure = mSharedDepartureViewModel.getDeparture().getValue();
    if (departure != null) {
      setAppBarLayout(departure.getHeadSign(), departure.getVehicleIdList().get(0));
      mBusRouteViewModel.init(departure.getTripList().get(0).getTripId());
    }

    mBusRouteViewModel.getRouteList().observe(this, new Observer<List<StopTimes>>() {
      @Override
      public void onChanged(@Nullable List<StopTimes> routeList) {
        if (routeList != null && departure != null) {
          mAdapter.updateStopTimesList(
            departure.getRouteList().get(0).getRouteColor(),
            departure.getHeadSign(),
            routeList
          );
        }
      }
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    mUnbinder.unbind();
  }
}
