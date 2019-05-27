package com.example.junyoung.culivebus.ui.route;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.httpclient.pojos.Departure;
import com.example.junyoung.culivebus.ui.common.NavigationController;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.android.DaggerActivity;
import dagger.android.support.DaggerAppCompatActivity;

public class RouteActivity extends DaggerAppCompatActivity {
  private static final String EXTRA_TRIP_ID = "TRIP_ID";
  private static final String EXTRA_SHAPE_ID = "SHAPE_ID";
  private static final String EXTRA_VEHICLE_ID = "VEHICLE_ID";
  private static final String EXTRA_HEADSIGN = "HEADSIGN";
  private static final String EXTRA_ROUTE_COLOR = "ROUTE_COLOR";

  @Inject
  NavigationController navigationController;

  public static Intent starterIntent(Context context, @NonNull Departure departure) {
    return new Intent(context, RouteActivity.class)
      .putExtra(EXTRA_TRIP_ID, departure.getTrip().getTripId())
      .putExtra(EXTRA_SHAPE_ID, departure.getTrip().getShapeId())
      .putExtra(EXTRA_VEHICLE_ID, departure.getVehicleId())
      .putExtra(EXTRA_HEADSIGN, departure.getHeadsign())
      .putExtra(EXTRA_ROUTE_COLOR, departure.getRoute().getRouteColor());
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_route);

    if (savedInstanceState == null) {
      String tripId = getIntent().getStringExtra(EXTRA_TRIP_ID);
      String shapeId = getIntent().getStringExtra(EXTRA_SHAPE_ID);
      String vehicleId = getIntent().getStringExtra(EXTRA_VEHICLE_ID);
      String headsign = getIntent().getStringExtra(EXTRA_HEADSIGN);
      String routeColor = getIntent().getStringExtra(EXTRA_ROUTE_COLOR);

      navigationController.navigateToRoute(tripId, shapeId, vehicleId, headsign, routeColor);
    }
  }
}
