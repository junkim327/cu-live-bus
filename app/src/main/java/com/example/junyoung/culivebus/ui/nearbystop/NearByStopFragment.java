package com.example.junyoung.culivebus.ui.nearbystop;

import android.Manifest;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.junyoung.culivebus.databinding.NearByStopFragmentBinding;
import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.example.junyoung.culivebus.ui.MainNavigationFragment;
import com.example.junyoung.culivebus.ui.common.NavigationController;
import com.example.junyoung.culivebus.util.AutoClearedValue;
import com.google.android.gms.maps.MapView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemInfoWindowClickListener;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;

public class NearByStopFragment extends DaggerFragment implements MainNavigationFragment,
  OnClusterClickListener<StopPoint>,
  OnClusterItemClickListener<StopPoint>,
  OnClusterItemInfoWindowClickListener<StopPoint>,
  OnMyLocationButtonClickListener {
  private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
  private static final LatLng DEFAULT_LATLNG = new LatLng(40.109659, -88.227159);

  @Inject
  ViewModelProvider.Factory viewModelFactory;
  @Inject
  NavigationController navigationController;



  private GoogleMap map;
  private MapView mapView;
  private Bundle mapViewBundle;
  private NearByStopViewModel nearByStopViewModel;
  private BottomSheetBehavior bottomSheetBehavior;
  private ClusterManager<StopPoint> clusterManager;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    Timber.d("onCreate has been called");
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    Timber.d("onCreateView has been called");

    nearByStopViewModel = ViewModelProviders.of(this, viewModelFactory)
      .get(NearByStopViewModel.class);
    NearByStopFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.fragment_near_by_stop, container, false);
    dataBinding.setNearByStopViewModel(nearByStopViewModel);
    dataBinding.setLifecycleOwner(getViewLifecycleOwner());

    dataBinding.bottomSheet.departureButton.setOnClickListener(view -> {
      StopPoint stopPoint = nearByStopViewModel.getClickedStopPoint().getValue();
      if (stopPoint != null) {
        navigationController.navigateToDepartureActivity(stopPoint);
      }
    });

    mapView = dataBinding.mapView;
    mapView.onCreate(mapViewBundle);
    initializeMap();

    // Initialize bottom sheet behavior.
    bottomSheetBehavior = BottomSheetBehavior.from(dataBinding.bottomSheet.layout);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
      @Override
      public void onStateChanged(@NonNull View view, int i) {

      }

      @Override
      public void onSlide(@NonNull View view, float slideOffset) {
        // Calculate the bottom padding of map view.
        int bottomSheetHeight = dataBinding.bottomSheet.layout.getHeight();
        if (bottomSheetHeight != 0) {
          int paddingBottom = (int) (bottomSheetHeight * slideOffset);
          nearByStopViewModel.setMapPaddingBottom(paddingBottom);
        }
      }
    });

    return dataBinding.getRoot();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

    Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
    if (mapViewBundle == null) {
      mapViewBundle = new Bundle();
      outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
    }

    mapView.onSaveInstanceState(mapViewBundle);
  }

  @Override
  public Boolean onBackPressed() {
    return null;
  }

  @Override
  public void onStart() {
    Timber.d("onStart has been called");
    super.onStart();
    mapView.onStart();
  }

  @Override
  public void onResume() {
    Timber.d("onResume has called.");
    super.onResume();
    mapView.onResume();
  }

  @Override
  public void onStop() {
    Timber.d("onStop has been called");
    super.onStop();
    mapView.onStop();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  public boolean onMyLocationButtonClick() {
    return false;
  }

  @Override
  public boolean onClusterClick(Cluster<StopPoint> cluster) {
    nearByStopViewModel.setClusterClickEvent(cluster.getPosition());
    return true;
  }

  @Override
  public boolean onClusterItemClick(StopPoint stopPoint) {
    nearByStopViewModel.onClusterItemClick(stopPoint);
    return false;
  }

  @Override
  public void onClusterItemInfoWindowClick(StopPoint stopPoint) {
    navigationController.navigateToDepartureActivity(stopPoint);
  }

  private void initializeMap() {
    mapView.getMapAsync(googleMap -> {
      map = googleMap;
      checkLocationPermission();
      googleMap.setInfoWindowAdapter(new BusStopInfoWindowAdapter(getActivity()));

      nearByStopViewModel.getBusStops().observe(getViewLifecycleOwner(), new Observer<List<StopPoint>>() {
        @Override
        public void onChanged(List<StopPoint> stopPoints) {
          initClusterManager(stopPoints);
        }
      });
    });
  }

  private void initClusterManager(List<StopPoint> stopPoints) {
    clusterManager = new ClusterManager<>(getActivity(), map);
    clusterManager.setRenderer(new BusStopMarkerRenderer(getContext(), map, clusterManager));

    map.setOnCameraIdleListener(clusterManager);
    map.setOnMarkerClickListener(clusterManager);
    map.setOnInfoWindowClickListener(clusterManager);
    clusterManager.setOnClusterClickListener(this);
    clusterManager.setOnClusterItemClickListener(this);
    clusterManager.setOnClusterItemInfoWindowClickListener(this);

    clusterManager.addItems(stopPoints);
    clusterManager.cluster();
  }

  private void checkLocationPermission() {
    if (getContext() != null && ContextCompat.checkSelfPermission(getContext(),
      Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      Timber.d("Permission Granted");
      map.setMyLocationEnabled(true);

      if (getActivity() != null) {
        FusedLocationProviderClient fusedLocationProviderClient =
          LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(),
          location -> {
            if (location != null) {
              Timber.d("moveCamera");
              LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
              nearByStopViewModel.setLatLng(userLatLng);
            } else {
              Timber.d("location null");
              nearByStopViewModel.setLatLng(DEFAULT_LATLNG);
            }
          });
      }
    } else {
      nearByStopViewModel.setLatLng(DEFAULT_LATLNG);
    }
  }
}
