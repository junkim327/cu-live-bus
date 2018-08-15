package com.example.junyoung.uiucbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusStopsInMapActivity extends AppCompatActivity implements OnMapReadyCallback {
  private static final String TAG = "BusStopsInMapActivity";

  private String userLatitude;
  private String userLongitude;
  private ArrayList<Stop> busStopList;

  private GoogleMap map;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bus_stops_in_map);

    Intent intent = getIntent();
    userLatitude = intent.getStringExtra("latitude");
    userLongitude = intent.getStringExtra("longitude");

    // Log.d(TAG, userLatitude + " " + userLongitude);

    BusStopsEndpoints service =
      RetrofitBuilder.getRetrofitInstance().create(BusStopsEndpoints.class);
    Call<BusStops> call = service.getNearestStops(
      Constants.API_KEY,
      userLatitude,
      userLongitude
    );

    call.enqueue(new Callback<BusStops>() {
      @Override
      public void onResponse(@NonNull Call<BusStops> call, @NonNull Response<BusStops> response) {
        if (response.isSuccessful()) {
          BusStops responseBody = response.body();
          if (responseBody != null) {
            busStopList = responseBody.getStops();
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

    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
      new LatLng(
        Double.valueOf(userLatitude),
        Double.valueOf(userLongitude)
      ), 15.0f), 1000, null);
  }

  private void createMarkers() {
    for (int i = 0; i < busStopList.size(); i++) {
      ArrayList<StopPoint> stopPoints = busStopList.get(i).getStopPoints();
      for (int j = 0; j < stopPoints.size(); j++) {
        LatLng markerPosition = new LatLng(
          stopPoints.get(j).getStopLat(),
          stopPoints.get(j).getStopLon()
        );
        map.addMarker(new MarkerOptions()
          .position(markerPosition)
          .title(busStopList.get(i).getStopName())
        );
      }
    }
  }
}
