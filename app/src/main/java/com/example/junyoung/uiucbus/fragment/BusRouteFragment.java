package com.example.junyoung.uiucbus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
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

import com.example.junyoung.uiucbus.DeviceDimensionsHelper;
import com.example.junyoung.uiucbus.OnHomeItemClickedListener;
import com.example.junyoung.uiucbus.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.RouteItemDecoration;
import com.example.junyoung.uiucbus.adapter.BusRouteAdapter;
import com.example.junyoung.uiucbus.httpclient.pojos.Location;
import com.example.junyoung.uiucbus.httpclient.pojos.Shape;
import com.example.junyoung.uiucbus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.uiucbus.httpclient.pojos.Vehicle;
import com.example.junyoung.uiucbus.ui.viewmodel.BusPathViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.BusRouteViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedDepartureViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedStopPointViewModel;
import com.example.junyoung.uiucbus.util.UtilConnection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BusRouteFragment extends Fragment implements OnMapReadyCallback,
  GoogleMap.OnMarkerClickListener {
  private static final String TAG = BusRouteFragment.class.getSimpleName();

  private String mRouteColor;
  private boolean mIsInternetConnected;

  private MenuItem mMapMenuItem;

  private SortedDeparture mDeparture;

  private GoogleMap mMap;
  private Unbinder mUnbinder;
  private BottomSheetBehavior mBottomSheetBehavior;
  private BusRouteAdapter mAdapter;
  private ConnectivityManager mConnectivityManager;

  // ViewModels
  private BusPathViewModel mBusPathViewModel;
  private BusRouteViewModel mBusRouteViewModel;
  private SharedStopPointViewModel mSharedStopPointViewModel;
  private SharedDepartureViewModel mSharedDepartureViewModel;

  // Callbacks
  private OnHomeItemClickedListener mHomeItemClickedCallback;
  private OnInternetConnectedListener mInternetConnectedCallback;

  @BindView(R.id.toolbar_bus_routes)
  Toolbar mToolbar;
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
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "onCreate has called");

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );

    mSharedDepartureViewModel = ViewModelProviders.of(getActivity())
      .get(SharedDepartureViewModel.class);
    mDeparture = mSharedDepartureViewModel.getDeparture().getValue();
    if (mDeparture != null) {
      mRouteColor = mDeparture.getRouteList().get(0).getRouteColor();
    }

    mBusPathViewModel = ViewModelProviders.of(this).get(BusPathViewModel.class);

    mBusRouteViewModel = ViewModelProviders.of(this).get(BusRouteViewModel.class);
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

      SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
        .findFragmentById(R.id.map_bus_route);
      mapFragment.getMapAsync(this);

      setToolbar();
      setRecyclerView();
      mBottomSheetBehavior = BottomSheetBehavior.from(mRecyclerView);

      int peekHeight = DeviceDimensionsHelper.getScreenHeight(getActivity()) / 3;
      mBottomSheetBehavior.setPeekHeight(peekHeight);
      mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    return view;
  }

  private void setToolbar() {
    mToolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      setHasOptionsMenu(true);
      actionBar.setTitle("Route");
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }
  }

  private void setRecyclerView() {
    mRecyclerView.setHasFixedSize(true);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerView.setLayoutManager(layoutManager);

    mRecyclerView.addItemDecoration(new RouteItemDecoration(
      getContext(),
      getResources().getString(R.string.hex_color, mRouteColor))
    );

    mAdapter = new BusRouteAdapter(getContext());
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    mMap.setPadding(0, 0, 0, DeviceDimensionsHelper.getScreenHeight(getActivity()) / 3);
    mMap.setMinZoomPreference(14.0f);
    mMap.setMaxZoomPreference(17.0f);

    if (mDeparture != null) {
      mBusPathViewModel.init(mDeparture.getTripList().get(0).getShapeId());
      mBusRouteViewModel.initBusLocation(mDeparture.getVehicleIdList().get(0));
    }

    mMap.setOnMarkerClickListener(this);
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    Log.d(TAG, "Marker index: " + (int) marker.getTag());
    mRecyclerView.smoothScrollToPosition((int) marker.getTag());
    return false;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Log.d(TAG, "onActivityCreated has called");

    mSharedStopPointViewModel = ViewModelProviders.of(getActivity())
      .get(SharedStopPointViewModel.class);

    mBusPathViewModel.getBusPath().observe(this, path -> {
      // Update UI
      if (path != null) {
        LatLngBounds bound = drawPolyline(path.getShapes());
        mMap.setOnMapLoadedCallback(() -> {
          if (bound != null) {
            Log.d(TAG, "onMapLoaded has called");
            mMap.setLatLngBoundsForCameraTarget(bound);
          }
        });
      }
    });

    mBusRouteViewModel.getBusLocation().observe(this, vehicle -> {
      if (vehicle != null) {
        drawBusMarker(vehicle);
      }
    });

    if (mDeparture != null) {
      mBusRouteViewModel.initRouteList(mDeparture.getTripList().get(0).getTripId());
    }

    mBusRouteViewModel.getRouteList().observe(this, routeList -> {
      if (routeList != null && mDeparture != null) {
        String busStopName = getResources().getString(R.string.empty_string);
        if (mSharedStopPointViewModel.getSelectedStopPoint().getValue() != null) {
          busStopName = mSharedStopPointViewModel.getSelectedStopPoint().getValue().getStopName();
        }
        mAdapter.updateStopTimesList(mRouteColor, busStopName, routeList);

        int pos = 0;
        for (int i = 0; i < routeList.size(); i++) {
          if (routeList.get(i).getStopPoint().getStopName().contentEquals(busStopName)) {
            pos = i;
          }
        }
        mRecyclerView.scrollToPosition(pos);
      }
    });
  }

  private void drawBusMarker(Vehicle vehicle) {
    if (vehicle.getTrip() != null && vehicle.getLocation() != null) {
      Location busLocation = vehicle.getLocation();
      MarkerOptions options = new MarkerOptions()
        .position(new LatLng(busLocation.getLat(), busLocation.getLon()))
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_side_color))
        .zIndex(1.0f)
        .flat(true);

      if (vehicle.getTrip().getDirection().contentEquals("West")) {
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_side_invert_color));
      } else if (vehicle.getTrip().getDirection().contentEquals("North")) {
        options.rotation(270);
      } else if (vehicle.getTrip().getDirection().contentEquals("South")) {
        options.rotation(90);
      }

      mMap.addMarker(options);
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(busLocation.getLat(),
        busLocation.getLon()), 15));
    }
  }

  private LatLngBounds drawPolyline(List<Shape> shapeList) {
    if (shapeList != null && shapeList.size() > 0) {
      int busStopIndex = 0;
      LatLngBounds.Builder builder = LatLngBounds.builder();
      PolylineOptions polylineOptions = new PolylineOptions();
      for (int i = 0; i < shapeList.size(); i++) {
        Shape shape = shapeList.get(i);
        LatLng point = new LatLng(shape.getShapeLat(), shape.getShapeLon());
        if (shape.getStopId() != null) {
          mMap.addMarker(new MarkerOptions()
            .position(point)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.google_map_custom_marker))
          ).setTag(busStopIndex++);
        }
        builder.include(point);
        polylineOptions.add(point);
      }

      polylineOptions
        .width(20)
        .color(Color.parseColor(getResources().getString(R.string.hex_color, mRouteColor)))
        .geodesic(true);

      Polyline polyline = mMap.addPolyline(polylineOptions);
      polyline.setStartCap(new RoundCap());
      polyline.setEndCap(new RoundCap());
      polyline.setJointType(JointType.ROUND);

      Log.d(TAG, "Polyline was drawn");

      return builder.build();
    }

    return null;
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume has called");
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.home_menu, menu);
    if (menu != null) {
      mMapMenuItem = menu.findItem(R.id.action_map_bus_route);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        return true;
      case R.id.action_home:
        mHomeItemClickedCallback.onHomeItemClicked();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
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
