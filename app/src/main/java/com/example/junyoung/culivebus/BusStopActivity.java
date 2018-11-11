package com.example.junyoung.culivebus;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.junyoung.culivebus.fragment.BusDeparturesFragment;
import com.example.junyoung.culivebus.fragment.BusRouteFragment;
import com.example.junyoung.culivebus.fragment.NoInternetConnectionFragment;
import com.example.junyoung.culivebus.httpclient.pojos.Stop;
import com.example.junyoung.culivebus.room.entity.StopPoint;
import com.example.junyoung.culivebus.ui.nearbystop.NearByStopFragment;
import com.example.junyoung.culivebus.ui.viewmodel.BusStopViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SharedStopPointViewModel;
import com.example.junyoung.culivebus.util.listener.OnHomeItemClickedListener;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BusStopActivity extends AppCompatActivity implements
  OnHomeItemClickedListener,
  OnInternetConnectedListener,
  NearByStopFragment.OnBusStopClickedListener,
  BusDeparturesFragment.OnDepartureClickedListener {
  public static final String TAG = "BusStopActivity";
  private static final String MAP_FRAGMENT_TAG = "mMap";

  private SharedStopPointViewModel mSharedStopPointViewModel;

  private BusRouteFragment mBusRouteFragment;
  private NearByStopFragment mNearByStopFragment;
  private BusDeparturesFragment mBusDeparturesFragment;
  private NoInternetConnectionFragment mNoInternetConnectionFragment;

  @BindView(R.id.framelayout_bus_stops_in_map)
  FrameLayout mContainer;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bus_stops_in_map);
    ButterKnife.bind(this);

    mSharedStopPointViewModel = ViewModelProviders.of(this).get(SharedStopPointViewModel.class);

    mNearByStopFragment = new NearByStopFragment();
    setFragment(mNearByStopFragment);
  }


  @Override
  protected void onResume() {
    Log.d(TAG, "onResume has started");
    super.onResume();
  }

  /*
  private void createUserMarker() {
    Marker userMarker = mMap.addMarker(new MarkerOptions()
      .position(new LatLng(Double.valueOf(userLatitude), Double.valueOf(userLongitude)))
      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
    );

    userMarkerId = userMarker.getId();
  }*/

  private void setFragment(Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
      .add(mContainer.getId(), fragment)
      .commit();
  }

  private void replaceFragment(Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
      .replace(mContainer.getId(), fragment)
      .commit();
  }

  @Override
  public void onHomeItemClicked() {
    NavUtils.navigateUpFromSameTask(this);
  }

  @Override
  public void onInternetConnected(boolean isConnected, boolean shouldHideFragment) {
    if (!isConnected) {
      if (mNoInternetConnectionFragment == null) {
        mNoInternetConnectionFragment = new NoInternetConnectionFragment();
        replaceFragment(mNoInternetConnectionFragment);
      }
    }
  }

  @Override
  public void onBusStopClicked() {
    if (mBusDeparturesFragment == null) {
      mBusDeparturesFragment = new BusDeparturesFragment();
    }

    replaceFragment(mBusDeparturesFragment);
  }

  @Override
  public void onDepartureClicked() {
    if (mBusRouteFragment == null) {
      mBusRouteFragment = new BusRouteFragment();
    }
    //performFragmentOperation(mBusRouteFragment);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }
}

