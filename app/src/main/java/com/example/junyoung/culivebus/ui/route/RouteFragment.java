package com.example.junyoung.culivebus.ui.route;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.junyoung.culivebus.binding.FragmentDataBindingComponent;
import com.example.junyoung.culivebus.databinding.RouteFragmentBinding;
import com.example.junyoung.culivebus.util.AutoClearedValue;
import com.example.junyoung.culivebus.vo.Resource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Circle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.vo.StopTime;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.httpclient.pojos.Location;
import com.example.junyoung.culivebus.httpclient.pojos.Vehicle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;

public class RouteFragment extends DaggerFragment implements GoogleMap.OnMarkerClickListener,
  GoogleMap.OnCircleClickListener {
  private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
  private static final String TAG = RouteFragment.class.getSimpleName();
  private static final String EXTRA_TRIP_ID = "TRIP_ID";
  private static final String EXTRA_SHAPE_ID = "SHAPE_ID";
  private static final String EXTRA_VEHICLE_ID = "VEHICLE_ID";
  private static final String EXTRA_HEADSIGN = "HEADSIGN";
  private static final String EXTRA_ROUTE_COLOR = "ROUTE_COLOR";

  @Inject
  ViewModelProvider.Factory viewModelFactory;

  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<RouteFragmentBinding> binding;


  private GoogleMap map;
  private MapView mapView;
  private Bundle mapViewBundle;
  private RouteListAdapter adapter;
  private RouteViewModel routeViewModel;

  private boolean mDoesUserJustClickedRecyclerView = false;

  private LatLng busLocation;
  private Marker mCircleClickedIndicatorMarker;

  private BottomSheetBehavior bottomSheetBehavior;
  private ConnectivityManager mConnectivityManager;
  private Marker busMarker;

  // ViewModels

  public static RouteFragment newInstance(String tripId, String shapeId, String vehicleId,
                                          String headSign, String routeColor) {
    RouteFragment fragment = new RouteFragment();
    Bundle bundle = new Bundle();
    bundle.putString(EXTRA_TRIP_ID, tripId);
    bundle.putString(EXTRA_SHAPE_ID, shapeId);
    bundle.putString(EXTRA_VEHICLE_ID, vehicleId);
    bundle.putString(EXTRA_HEADSIGN, headSign);
    bundle.putString(EXTRA_ROUTE_COLOR, routeColor);
    fragment.setArguments(bundle);

    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
    }

    setHasOptionsMenu(true);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    routeViewModel = ViewModelProviders.of(this, viewModelFactory).get(RouteViewModel.class);
    RouteFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.fragment_route, container, false, dataBindingComponent);

    dataBinding.setViewModel(routeViewModel);
    dataBinding.setLifecycleOwner(getViewLifecycleOwner());

    Bundle bundle = getArguments();
    if (bundle != null) {
      routeViewModel.setTripId(bundle.getString(EXTRA_TRIP_ID));
      routeViewModel.setShapeId(bundle.getString(EXTRA_SHAPE_ID));
      routeViewModel.setVehicleId(bundle.getString(EXTRA_VEHICLE_ID));
      routeViewModel.setHeadsign(bundle.getString(EXTRA_HEADSIGN));
      routeViewModel.setRouteColor(bundle.getString(EXTRA_ROUTE_COLOR));
      dataBinding.busRouteList.addItemDecoration(new RouteItemDecoration(getContext(),
        bundle.getString(EXTRA_ROUTE_COLOR)));
    }

    adapter = new RouteListAdapter(routeViewModel, getViewLifecycleOwner());
    dataBinding.busRouteList.setAdapter(adapter);

    mapView = dataBinding.mapViewBusRoute;
    mapView.onCreate(mapViewBundle);
    initializeMap();

    bottomSheetBehavior = BottomSheetBehavior.from(dataBinding.busRouteList);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    binding = new AutoClearedValue<>(this, dataBinding);

    return dataBinding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    Timber.d("onViewCreated has been called");

    initToolbar();

    routeViewModel.loadStopTimeResult().observe(getViewLifecycleOwner(),
      new Observer<Resource<List<StopTime>>>() {
      @Override
      public void onChanged(Resource<List<StopTime>> resource) {
        processStopTimesResponse(resource);
      }
    });

    routeViewModel.loadVehicleResult().observe(getViewLifecycleOwner(),
      new Observer<Resource<List<Vehicle>>>() {
        @Override
        public void onChanged(Resource<List<Vehicle>> listResource) {
          processVehicleResponse(listResource);
        }
      });
  }

  private void onRecyclerViewClicked(View view, int pos) {
    mDoesUserJustClickedRecyclerView = true;

//    if (bottomSheetBehavior != null) {
//      updateClickedBusStopInfo(pos - 2);
//
//      StopPoint stopPoint = adapter.getClickedStop(pos - 2).getStopPoint();
//      createBusStopMarker(new LatLng(stopPoint.getLatitude(), stopPoint.getLongitude()));
//      animateCamera(new LatLng(stopPoint.getLatitude(), stopPoint.getLongitude()));
//      recyclerView.scrollToPosition(0);
//      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//    }
  }

  private void updateClickedBusStopInfo(int pos) {
//    if (adapter != null) {
//      StopTime stop = adapter.getClickedStop(pos);
//      if (stop != null) {
//        binding.get().setStopPoint(stop.getStopPoint());
//      }
//    }
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    Log.d(TAG, "Marker was clicked");
    //mBusStopClickedCallback.onBusStopClicked();
    return false;
  }

  /**
   *  Create a marker at the center of circle.
   *
   *  If the marker is already created by clicking the circle, then first remove the marker,
   *  and create a new one.
   *
   * @param circle The circle that is clicked.
   */
  @Override
  public void onCircleClick(Circle circle) {
    Log.d(TAG, "Circle has been clicked");

    createBusStopMarker(circle.getCenter());
    updateClickedBusStopInfo((int) circle.getTag());
    animateCamera(circle.getCenter());
  }

  private void createBusStopMarker(LatLng markerPosition) {
    if (mCircleClickedIndicatorMarker != null) {
      mCircleClickedIndicatorMarker.remove();
    }
    mCircleClickedIndicatorMarker = map.addMarker(new MarkerOptions().position(markerPosition)
      .icon(bitmapDescriptorFromVector()));
  }

  private BitmapDescriptor bitmapDescriptorFromVector() {
    Drawable vectorDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_bus_stop_color);
    vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
    Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    vectorDrawable.draw(canvas);
    return BitmapDescriptorFactory.fromBitmap(bitmap);
  }

//  private void drawPolyline(List<Shape> shapeList) {
//    if (shapeList != null && shapeList.size() > 0) {
//      int busStopIndex = 0;
//      PolylineOptions polylineOptions = new PolylineOptions();
//      for (int i = 0; i < shapeList.size(); i++) {
//        Shape shape = shapeList.get(i);
//        LatLng point = new LatLng(shape.getShapeLat(), shape.getShapeLon());
//        if (shape.getStopId() != null) {
//          Circle circle = map.addCircle(new CircleOptions()
//            .center(point)
//            .strokeColor(Color.parseColor(routeColor))
//            .radius(32)
//            .fillColor(mWhite)
//            .zIndex(10)
//          );
//
//          circle.setClickable(true);
//          circle.setTag(busStopIndex++);
//        }
//        polylineOptions.add(point);
//      }
//
//      polylineOptions
//        .width(20)
//        .color(Color.parseColor(routeColor))
//        .geodesic(true);
//
//      Polyline polyline = map.addPolyline(polylineOptions);
//      polyline.setStartCap(new RoundCap());
//      polyline.setEndCap(new RoundCap());
//      polyline.setJointType(JointType.ROUND);
//
//      Log.d(TAG, "Polyline was drawn");
//    }
//  }


  @Override
  public void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
    Log.d(TAG, "onResume has called");

//    bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//      @SuppressLint("SwitchIntDef")
//      @Override
//      public void onStateChanged(@NonNull View view, int newState) {
//        switch (newState) {
//          case BottomSheetBehavior.STATE_COLLAPSED:
//            if (!mDoesUserJustClickedRecyclerView && busLocation != null) {
//              animateCamera(busLocation);
//            }
//            mDoesUserJustClickedRecyclerView = false;
//            break;
//          case BottomSheetBehavior.STATE_EXPANDED:
//            binding.get().busStopInfoCard.layout.setVisibility(GONE);
//            break;
//          default:
//            break;
//        }
//      }
//
//      @Override
//      public void onSlide(@NonNull View view, float v) {
//
//      }
//    });
  }

  @Override
  public void onStop() {
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
    if (mapView != null) {
      mapView.onDestroy();
    }
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.home_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_home) {
      //mainNavigationController.navigateToDashboard();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void initializeMap() {
    mapView.getMapAsync(googleMap -> {
      map = googleMap;
      googleMap.setPadding(
        (int) getResources().getDimension(R.dimen.small_size_toolbar_height),
        0,
        0,
        bottomSheetBehavior.getPeekHeight()
      );
      googleMap.setOnMarkerClickListener(this);
      googleMap.setOnCircleClickListener(this);
    });
  }

  private void initToolbar() {
    Toolbar toolbar = binding.get().toolbarBusRoutes;
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);

    toolbar.setNavigationOnClickListener(view -> {
      FragmentManager fm = getFragmentManager();
      if (fm != null && fm.getBackStackEntryCount() > 0) {
        fm.popBackStack();
      }
    });
  }

  private void animateCamera(LatLng latLngPosition) {
    if (map != null) {
      map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPosition, 15), 500, null);
    }
  }

  private void processVehicleResponse(Resource<List<Vehicle>> resource) {
    switch (resource.status) {
      case SUCCESS:
        if (resource.data != null && !resource.data.isEmpty()) {
          Timber.d("Vehicle Response Status : Success");
          drawBusMarker(resource.data.get(0));
//          map.setOnMapLoadedCallback(() -> {
//            Timber.d("onMapLoaded has called");
//            map.moveCamera(CameraUpdateFactory.newLatLng(busLocation));
//            map.animateCamera(CameraUpdateFactory.zoomTo(15), 1500, null);
//          });
        }
        break;
      case ERROR:

        break;
      default:
        break;
    }
  }

  private void drawBusMarker(@NonNull Vehicle vehicle) {
    if (vehicle.getTrip() != null && vehicle.getLocation() != null) {
      Location busLocation = vehicle.getLocation();
      this.busLocation = new LatLng(busLocation.getLat(), busLocation.getLon());

      if (busMarker != null) {
        busMarker.setPosition(this.busLocation);
        //animateCamera(busLocation);
        routeViewModel.setShouldAnimate(true);
        routeViewModel.setLatLng(this.busLocation);
      } else {
        MarkerOptions options = new MarkerOptions()
          .position(this.busLocation)
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_side_color))
          .zIndex(1.0f)
          .flat(true);

        if (map != null) {
          busMarker = map.addMarker(options);
          routeViewModel.setLatLng(this.busLocation);
          //map.moveCamera(CameraUpdateFactory.newLatLng(busLocation));
        }
      }

      setBusMarkerDirection(vehicle.getTrip().getDirection());
    }
  }

  private void setBusMarkerDirection(String direction) {
    Log.d(TAG, "Current Direction : " + direction);
    if (busMarker != null) {
      if (direction.startsWith("W")) {
        busMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_side_invert_color));
      } else if (direction.startsWith("N")) {
        busMarker.setRotation(270);
      } else if (direction.startsWith("S")) {
        busMarker.setRotation(90);
      }
    }
  }

  private void processStopTimesResponse(Resource<List<StopTime>> resource) {
    Timber.d("StopTimesResponse status : %s", resource.status.toString());
    switch (resource.status) {
      case LOADING:
        break;
      case SUCCESS:
        adapter.updateStopTimes(resource.data);
        break;
      case ERROR:
        break;
    }
  }
}
