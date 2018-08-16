package com.example.junyoung.uiucbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.endpoints.BusStopsEndpoints;
import com.example.junyoung.uiucbus.httpclient.pojos.BusStops;
import com.example.junyoung.uiucbus.httpclient.pojos.Stop;
import com.example.junyoung.uiucbus.httpclient.pojos.StopPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusStopsInMapActivity extends AppCompatActivity implements
  GoogleMap.OnMarkerClickListener,
  GoogleMap.OnInfoWindowClickListener,
  OnMapReadyCallback {
  private static final String TAG = "BusStopsInMapActivity";
  public static final String EXTRA_STOPID = "com.example.junyoung.uiucbus.EXTRA_STOPID";

  private String userLatitude;
  private String userLongitude;
  private ArrayList<Stop> busStopList;

  private GoogleMap map;

  @BindView(R.id.bottom_sheet_in_bus_stops_activity)
  RelativeLayout bottomSheet;
  @BindView(R.id.textview_bus_stop_name_bottom_sheet)
  TextView busStopNameTextView;
  @BindView(R.id.textview_bus_stop_code_bottom_sheet)
  TextView busStopCodeTextView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bus_stops_in_map);
    ButterKnife.bind(this);

    Intent intent = getIntent();
    userLatitude = intent.getStringExtra("latitude");
    userLongitude = intent.getStringExtra("longitude");

    BottomSheetBehavior bottomSheetBehavior =
      BottomSheetBehavior.from(bottomSheet);

    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    // Log.d(TAG, userLatitude + " " + userLongitude);

    BusStopsEndpoints service =
      RetrofitBuilder.getRetrofitInstance().create(BusStopsEndpoints.class);
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

    createMarkers();

    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
      new LatLng(
        Double.valueOf(userLatitude),
        Double.valueOf(userLongitude)
      ),
      16.0f)
    );

    map.setOnMarkerClickListener(this);
    map.setOnInfoWindowClickListener(this);
  }

  private void createMarkers() {
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
        );
        marker.setTag(stopPoints.get(j));
      }
    }
  }

  @Override
  public boolean onMarkerClick(final Marker marker) {
    StopPoint stopPointInfo = (StopPoint) marker.getTag();

    if (stopPointInfo != null) {
      busStopNameTextView.setText(stopPointInfo.getStopName());
      busStopCodeTextView.setText(stopPointInfo.getCode());
    }

    BottomSheetBehavior bottomSheetBehavior =
      BottomSheetBehavior.from(bottomSheet);

    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    return false;
  }

  @Override
  public void onInfoWindowClick(final Marker marker) {
    StopPoint stopPointInfo = (StopPoint) marker.getTag();

    Intent intent = new Intent(this, BusDeparturesActivity.class);
    if (stopPointInfo != null) {
      intent.putExtra(EXTRA_STOPID, stopPointInfo.getStopId());
    }
    startActivity(intent);
  }
}
