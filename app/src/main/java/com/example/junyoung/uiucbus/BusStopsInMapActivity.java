package com.example.junyoung.uiucbus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.junyoung.uiucbus.adapters.BusStopInfoWindowAdapter;
import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.services.BusStopsServices;
import com.example.junyoung.uiucbus.httpclient.pojos.BusStops;
import com.example.junyoung.uiucbus.httpclient.pojos.Stop;
import com.example.junyoung.uiucbus.httpclient.pojos.StopPoint;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusStopsInMapActivity extends AppCompatActivity implements
  GoogleMap.OnMarkerClickListener,
  GoogleMap.OnInfoWindowClickListener,
  OnCameraIdleListener,
  OnMapReadyCallback,
  View.OnClickListener {
  public static final String TAG = "BusStopsInMapActivity";
  public static final String EXTRA_CODE = "com.example.junyoung.uiucbus.EXTRA_CODE";
  public static final String EXTRA_STOPID = "com.example.junyoung.uiucbus.EXTRA_STOPID";
  public static final String EXTRA_STOPNAME = "com.example.junyoung.uiucbus.EXTRA_STOPNAME";

  private String userMarkerId;
  private String userLatitude;
  private String userLongitude;
  private String userSelectedBusId;
  private String userSelectedBusStopCode;
  private String userSelectedBusStopName;
  private ArrayList<Stop> busStopList;

  private GoogleMap map;
  private BottomSheetBehavior bottomSheetBehavior;
  private FusedLocationProviderClient mFusedLocationClient;

  @BindView(R.id.toolbar_bus_stops_in_map)
  Toolbar busStopsInMapToolBar;
  @BindView(R.id.textview_distance_bottom_sheet)
  TextView distanceTextView;
  @BindView(R.id.bottom_sheet_in_bus_stops_activity)
  RelativeLayout bottomSheet;
  @BindView(R.id.button_bus_departures_bottom_sheet)
  ImageButton busDepartureButton;
  @BindView(R.id.textview_bus_stop_name_bottom_sheet)
  TextView busStopNameTextView;
  @BindView(R.id.textview_bus_stop_code_bottom_sheet)
  TextView busStopCodeTextView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bus_stops_in_map);
    ButterKnife.bind(this);

    setToolbar();

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
      != PackageManager.PERMISSION_GRANTED) {
      userLatitude = getString(R.string.illini_union_latitude);
      userLongitude = getString(R.string.illini_union_longitude);
      getNearbyStops();
    } else {
      mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
      mFusedLocationClient.getLastLocation()
        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
          @Override
          public void onSuccess(Location location) {
            if (location != null) {
              userLatitude = String.valueOf(location.getLatitude());
              userLongitude = String.valueOf(location.getLongitude());
            } else {
              userLatitude = getString(R.string.illini_union_latitude);
              userLongitude = getString(R.string.illini_union_longitude);
            }
            getNearbyStops();
          }
        });
    }

    bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    // Log.d(TAG, userLatitude + " " + userLongitude);

    busDepartureButton.setOnClickListener(this);
  }

  private void setToolbar() {
    setSupportActionBar(busStopsInMapToolBar);

    busStopsInMapToolBar.setTitleTextColor(Color.WHITE);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
      actionBar.setTitle("Nearby Stops");
    }
  }

  private void getNearbyStops() {
    BusStopsServices service =
      RetrofitBuilder.getRetrofitInstance().create(BusStopsServices.class);
    Call<BusStops> call = service.getNearestStops(
      Constants.API_KEY,
      userLatitude,
      userLongitude,
      "10"
    );

    call.enqueue(new Callback<BusStops>() {
      @Override
      public void onResponse(@NonNull Call<BusStops> call, @NonNull Response<BusStops> response) {
        if (response.isSuccessful()) {
          BusStops responseBody = response.body();
          if (responseBody != null) {
            busStopList = responseBody.getStops();
            Log.d(TAG, String.valueOf(busStopList.size()));
          }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
          .findFragmentById(R.id.map_bus_stops);
        mapFragment.getMapAsync(BusStopsInMapActivity.this);
      }

      @Override
      public void onFailure(@NonNull Call<BusStops> call, @NonNull Throwable t) {

      }
    });
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    map = googleMap;

    map.setOnCameraIdleListener(this);

    // Add marker at user location.
    createUserMarker();
    // Add markers at bus stop locations.
    createBusStopMarkers(busStopList);

    BusStopInfoWindowAdapter adapter = new BusStopInfoWindowAdapter(this);
    map.setInfoWindowAdapter(adapter);

    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
      new LatLng(
        Double.valueOf(userLatitude),
        Double.valueOf(userLongitude)
      ),
      16.0f),
      500,
      null
    );

    map.setOnMarkerClickListener(this);
    map.setOnInfoWindowClickListener(this);
  }

  @Override
  public void onCameraIdle() {
    LatLng cameraCurrentLatLng = map.getCameraPosition().target;

    BusStopsServices service =
      RetrofitBuilder.getRetrofitInstance().create(BusStopsServices.class);
    Call<BusStops> call = service.getNearestStops(
      Constants.API_KEY,
      String.valueOf(cameraCurrentLatLng.latitude),
      String.valueOf(cameraCurrentLatLng.longitude),
      "10"
    );

    call.enqueue(new Callback<BusStops>() {
      @Override
      public void onResponse(Call<BusStops> call, Response<BusStops> response) {
        ArrayList<Stop> busStopList = response.body().getStops();
        createBusStopMarkers(busStopList);
      }

      @Override
      public void onFailure(Call<BusStops> call, Throwable t) {

      }
    });
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume has started");
    super.onResume();

    bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
      @Override
      public void onStateChanged(@NonNull View bottomSheet, int newState) {

      }

      @Override
      public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        if (slideOffset == -1) {
          map.setPadding(0, 0, 0, -bottomSheet.getHeight());
        }
      }
    });
  }

  private void createUserMarker() {
    Marker userMarker = map.addMarker(new MarkerOptions()
      .position(new LatLng(Double.valueOf(userLatitude), Double.valueOf(userLongitude)))
      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
    );

    userMarkerId = userMarker.getId();
  }

  private void createBusStopMarkers(ArrayList<Stop> busStopList) {
    for (int i = 0; i < busStopList.size(); i++) {
      ArrayList<StopPoint> stopPoints = busStopList.get(i).getStopPoints();
      for (int j = 0; j < stopPoints.size(); j++) {
        LatLng markerPosition = new LatLng(
          stopPoints.get(j).getStopLat(),
          stopPoints.get(j).getStopLon()
        );
        Marker marker = map.addMarker(new MarkerOptions()
          .position(markerPosition)
          .title(stopPoints.get(j).getStopName())
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.google_map_custom_marker))
        );

        marker.setTag(stopPoints.get(j));
      }
    }
  }

  @Override
  public boolean onMarkerClick(final Marker marker) {
    if (marker.getId().equals(userMarkerId)) {
      marker.hideInfoWindow();

      return true;
    }

    StopPoint stopPointInfo = (StopPoint) marker.getTag();

    if (stopPointInfo != null) {
      userSelectedBusId = stopPointInfo.getStopId();
      userSelectedBusStopCode = stopPointInfo.getCode();
      userSelectedBusStopName = stopPointInfo.getStopName();
    }

    busStopNameTextView.setText(userSelectedBusStopName);
    busStopCodeTextView.setText(userSelectedBusStopCode);

    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    map.setPadding(0, 0, 0, bottomSheet.getHeight());

    return false;
  }

  @Override
  public void onInfoWindowClick(final Marker marker) {
    StopPoint stopPointInfo = (StopPoint) marker.getTag();

    if (stopPointInfo != null) {
      Intent intent = new Intent(this, BusDeparturesActivity.class);
      intent.putExtra(EXTRA_CODE, stopPointInfo.getCode());
      intent.putExtra(EXTRA_STOPID, stopPointInfo.getStopId());
      intent.putExtra(EXTRA_STOPNAME, stopPointInfo.getStopName());
      intent.putExtra(MainActivity.EXTRA_ACTIVITYNAME, TAG);
      startActivity(intent);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
      case R.id.action_home:
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.home_menu, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.button_bus_departures_bottom_sheet:
        Intent intent = new Intent(this, BusDeparturesActivity.class);

        intent.putExtra(EXTRA_STOPID, userSelectedBusId);
        intent.putExtra(EXTRA_CODE, userSelectedBusStopCode);
        intent.putExtra(EXTRA_STOPNAME, userSelectedBusStopName);
        intent.putExtra(MainActivity.EXTRA_ACTIVITYNAME, TAG);

        startActivity(intent);
    }
  }
}
