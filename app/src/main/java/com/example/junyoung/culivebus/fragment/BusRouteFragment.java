package com.example.junyoung.culivebus.fragment;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.junyoung.culivebus.room.entity.StopPoint;
import com.example.junyoung.culivebus.vo.Response;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Circle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.junyoung.culivebus.RouteItemDecoration;
import com.example.junyoung.culivebus.httpclient.pojos.StopTimes;
import com.example.junyoung.culivebus.util.listener.OnHomeItemClickedListener;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.adapter.BusRouteAdapter;
import com.example.junyoung.culivebus.httpclient.pojos.Location;
import com.example.junyoung.culivebus.httpclient.pojos.Shape;
import com.example.junyoung.culivebus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.culivebus.httpclient.pojos.Vehicle;
import com.example.junyoung.culivebus.ui.viewmodel.BusPathViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.BusRouteViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SharedDepartureViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SharedStopPointViewModel;
import com.example.junyoung.culivebus.util.UtilConnection;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BusRouteFragment extends Fragment implements OnMapReadyCallback,
  GoogleMap.OnMarkerClickListener,
  GoogleMap.OnCircleClickListener {
  private static final String TAG = BusRouteFragment.class.getSimpleName();
  private static final long UPDATE_INTERVAL_IN_MILLI = 40000L;

  private String mBusName;
  private String mBusCode;
  private String mRouteColor;
  private boolean mDoesUserJustClickedRecyclerView = false;
  private boolean mIsInternetConnected;
  private long mRecentVehicleUpdatedTime;

  private LatLng mBusLocation;
  private SortedDeparture mDeparture;
  private Marker mCircleClickedIndicatorMarker;

  private GoogleMap mMap;
  private Unbinder mUnbinder;
  private BottomSheetBehavior mBottomSheetBehavior;
  private BusRouteAdapter mAdapter;
  private ConnectivityManager mConnectivityManager;
  private Marker mBusMarker;

  // ViewModels
  private BusPathViewModel mBusPathViewModel;
  private BusRouteViewModel mBusRouteViewModel;
  private SharedStopPointViewModel mSharedStopPointViewModel;
  private SharedDepartureViewModel mSharedDepartureViewModel;

  // Callbacks
  private OnBusStopClickedListener mBusStopClickedCallback;
  private OnHomeItemClickedListener mHomeItemClickedCallback;
  private OnInternetConnectedListener mInternetConnectedCallback;

  @BindView(R.id.toolbar_bus_routes)
  Toolbar mToolbar;
  @BindView(R.id.map_view_bus_route)
  MapView mMapView;
  @BindView(R.id.fab_bus_routes)
  FloatingActionButton mFloatingActionButton;
  @BindView(R.id.recycler_view_bus_routes)
  RecyclerView mRecyclerView;

  @BindView(R.id.constraint_layout_bus_stop_info_bus_routes)
  ConstraintLayout mBusStopInfoLayout;
  @BindView(R.id.text_bus_stop_code_card_bus_stop_info)
  TextView mBusStopCodeTextView;
  @BindView(R.id.text_bus_stop_name_card_bus_stop_info)
  TextView mBusStopNameTextView;
  @BindView(R.id.button_bus_departures_card_bus_stop_info)
  ImageButton mBusDepartureButton;

  @BindDimen(R.dimen.bus_stop_info_card_height)
  int mBusStopInfoCardHeight;
  @BindDimen(R.dimen.bus_info_card_height)
  int mBusInfoCardHeight;
  @BindDimen(R.dimen.icon_height_and_width)
  int mBusStopMarkerHeight;
  @BindColor(R.color.white)
  int mWhite;

  public interface OnBusStopClickedListener {
    void onBusStopClicked();
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
      mBusStopClickedCallback = (OnBusStopClickedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnBusStopClickedListener.");
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
      mBusName = mDeparture.getHeadSign();
      mBusCode = mDeparture.getVehicleIdList().get(0);
    }

    mBusPathViewModel = ViewModelProviders.of(this).get(BusPathViewModel.class);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Log.d(TAG, "onActivityCreated has called");

    mBusRouteViewModel = ViewModelProviders.of(this).get(BusRouteViewModel.class);
    mSharedStopPointViewModel = ViewModelProviders.of(getActivity())
      .get(SharedStopPointViewModel.class);

    // Update UI
    mBusPathViewModel.getShapeResponse().observe(this, this::processShapeResponse);
    mBusRouteViewModel.getVehicleResponse().observe(this, this::processVehicleResponse);

    if (mDeparture != null) {
      mBusRouteViewModel.initRouteList(mDeparture.getTripList().get(0).getTripId());
    }

    mBusRouteViewModel.getRouteList().observe(this, routeList -> {
      if (routeList != null && mDeparture != null) {
        mAdapter.updateStopTimesList(mRouteColor, mBusName, mBusCode, routeList);
      }
    });

    if (getActivity() != null && mSharedStopPointViewModel == null) {
      mSharedStopPointViewModel = ViewModelProviders.of(getActivity())
        .get(SharedStopPointViewModel.class);
    }
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
      view = inflater.inflate(R.layout.fragment_bus_routes, container, false);
      mUnbinder = ButterKnife.bind(this, view);

      mMapView.onCreate(savedInstanceState);
      mMapView.getMapAsync(this);

      setToolbar();
      initRecyclerView();
      setupFloatingActionButton();

      mBottomSheetBehavior = BottomSheetBehavior.from(mRecyclerView);
      mBottomSheetBehavior.setPeekHeight(mBusStopInfoCardHeight + mBusInfoCardHeight);
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
      if (mDeparture != null) {
        actionBar.setTitle(mDeparture.getHeadSign());
      }
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }
  }

  private void setupFloatingActionButton() {
    mFloatingActionButton.setOnClickListener(view -> {
      mFloatingActionButton.startAnimation(AnimationUtils.loadAnimation(
        getContext(), R.anim.rotate));
      long currentTimeInMilli = Calendar.getInstance().getTimeInMillis();
      if ((currentTimeInMilli - mRecentVehicleUpdatedTime) > UPDATE_INTERVAL_IN_MILLI) {
        Log.d(TAG, "Vehicle updated");
        mBusRouteViewModel.initVehicle(mBusCode);
        mRecentVehicleUpdatedTime = currentTimeInMilli;
      }
      Log.d(TAG, "Time : " + mRecentVehicleUpdatedTime);
    });
  }

  private void initRecyclerView() {
    mRecyclerView.setHasFixedSize(true);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerView.setLayoutManager(layoutManager);

    mRecyclerView.addItemDecoration(new RouteItemDecoration(
      getContext(),
      getResources().getString(R.string.hex_color, mRouteColor))
    );

    RecyclerviewClickListener listener = this::onRecyclerViewClicked;
    mAdapter = new BusRouteAdapter(getContext(), listener);
    mRecyclerView.setAdapter(mAdapter);
  }

  private void onRecyclerViewClicked(View view, int pos) {
    mDoesUserJustClickedRecyclerView = true;

    if (mBottomSheetBehavior != null) {
      updateClickedBusStopInfo(pos - 2);

      StopPoint stopPoint = mAdapter.getClickedStop(pos - 2).getStopPoint();
      createBusStopMarker(new LatLng(stopPoint.getLatitude(), stopPoint.getLongitude()));
      animateCamera(new LatLng(stopPoint.getLatitude(), stopPoint.getLongitude()));
      mRecyclerView.scrollToPosition(0);
      mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
  }

  private void updateClickedBusStopInfo(int pos) {
    if (mAdapter != null) {
      StopTimes stop = mAdapter.getClickedStop(pos);
      if (stop != null) {
        mBusStopNameTextView.setText(stop.getStopPoint().getStopName());
        mBusStopCodeTextView.setText(stop.getStopPoint().getStopCode());
        mBusStopInfoLayout.setVisibility(VISIBLE);
        mSharedStopPointViewModel.select(stop.getStopPoint());
      }
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    Log.d(TAG, "onMapReady has called");
    mMap = googleMap;

    mMap.setPadding(0, 0, 0, mBusInfoCardHeight + mBusStopInfoCardHeight);
    mMap.setMinZoomPreference(14.0f);
    mMap.setMaxZoomPreference(17.0f);

    if (mDeparture != null) {
      mBusPathViewModel.initShapeList(mDeparture.getTripList().get(0).getShapeId());
      mBusRouteViewModel.initVehicle(mBusCode);
      mRecentVehicleUpdatedTime = Calendar.getInstance().getTimeInMillis();
      Log.d(TAG, "Time : " + mRecentVehicleUpdatedTime);
    }

    mMap.setOnMarkerClickListener(this);
    mMap.setOnCircleClickListener(this);
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    Log.d(TAG, "Marker was clicked");
    mBusStopClickedCallback.onBusStopClicked();
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
    mCircleClickedIndicatorMarker = mMap.addMarker(new MarkerOptions().position(markerPosition)
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

  private void animateCamera(LatLng latLngPosition) {
    if (mMap != null) {
      mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPosition, 15), 500, null);
    }
  }

  private void processShapeResponse(Response<List<Shape>> response) {
    switch (response.mStatus) {
      case SUCCESS:
        if (response.mData != null && !response.mData.isEmpty()) {
          drawPolyline(response.mData);
        }
        break;
      case ERROR:
        break;
      default:
        break;
    }
  }

  private void processVehicleResponse(Response<Vehicle> response) {
    switch (response.mStatus) {
      case SUCCESS:
        if (response.mData != null) {
          drawBusMarker(response.mData);
          mMap.setOnMapLoadedCallback(() -> {
            Log.d(TAG, "onMapLoaded has called");
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mBusLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1500, null);
          });
        }
        break;
      case ERROR:

        break;
    }
  }

  private void drawBusMarker(@NonNull Vehicle vehicle) {
    if (vehicle.getTrip() != null && vehicle.getLocation() != null) {
      Location busLocation = vehicle.getLocation();
      mBusLocation = new LatLng(busLocation.getLat(), busLocation.getLon());

      if (mBusMarker != null) {
        mBusMarker.setPosition(mBusLocation);
        animateCamera(mBusLocation);
      } else {
        MarkerOptions options = new MarkerOptions()
          .position(mBusLocation)
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_side_color))
          .zIndex(1.0f)
          .flat(true);

        if (mMap != null) {
          mBusMarker = mMap.addMarker(options);
          //mMap.moveCamera(CameraUpdateFactory.newLatLng(mBusLocation));
        }
      }

      setBusMarkerDirection(vehicle.getTrip().getDirection());
    }
  }

  private void setBusMarkerDirection(String direction) {
    Log.d(TAG, "Current Direction : " + direction);
    if (mBusMarker != null) {
      if (direction.startsWith("W")) {
      mBusMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_side_invert_color));
      } else if (direction.startsWith("N")) {
        mBusMarker.setRotation(270);
      } else if (direction.startsWith("S")) {
        mBusMarker.setRotation(90);
      }
    }
  }

  private void drawPolyline(List<Shape> shapeList) {
    if (shapeList != null && shapeList.size() > 0) {
      int busStopIndex = 0;
      PolylineOptions polylineOptions = new PolylineOptions();
      for (int i = 0; i < shapeList.size(); i++) {
        Shape shape = shapeList.get(i);
        LatLng point = new LatLng(shape.getShapeLat(), shape.getShapeLon());
        if (shape.getStopId() != null) {
          Circle circle = mMap.addCircle(new CircleOptions()
            .center(point)
            .strokeColor(Color.parseColor(getResources().getString(R.string.hex_color, mRouteColor)))
            .radius(32)
            .fillColor(mWhite)
            .zIndex(10)
          );

          circle.setClickable(true);
          circle.setTag(busStopIndex++);
        }
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
    }
  }


  @Override
  public void onStart() {
    super.onStart();
    mMapView.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
    mMapView.onResume();
    Log.d(TAG, "onResume has called");

    mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
      @SuppressLint("SwitchIntDef")
      @Override
      public void onStateChanged(@NonNull View view, int newState) {
        switch (newState) {
          case BottomSheetBehavior.STATE_COLLAPSED:
            if (!mDoesUserJustClickedRecyclerView && mBusLocation != null) {
              animateCamera(mBusLocation);
            }
            mDoesUserJustClickedRecyclerView = false;
            break;
          case BottomSheetBehavior.STATE_EXPANDED:
            mBusStopInfoLayout.setVisibility(GONE);
            break;
          default:
            break;
        }
      }

      @Override
      public void onSlide(@NonNull View view, float v) {

      }
    });
  }

  @OnClick(R.id.button_bus_departures_card_bus_stop_info)
  public void busDepartureButtonClick() {
    mBusStopClickedCallback.onBusStopClicked();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.home_menu, menu);
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
  public void onStop() {
    super.onStop();
    mMapView.onStop();
  }

  @Override
  public void onPause() {
    super.onPause();
    mMapView.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mMapView != null) {
      mMapView.onDestroy();
    }
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mMapView.onLowMemory();
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
