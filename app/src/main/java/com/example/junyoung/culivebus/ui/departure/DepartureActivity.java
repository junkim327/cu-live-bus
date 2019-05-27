package com.example.junyoung.culivebus.ui.departure;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.db.entity.StopPoint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.android.support.DaggerAppCompatActivity;

public class DepartureActivity extends DaggerAppCompatActivity {
  private static final int CONTAINER_ID = R.id.framelayout_departure;
  private static final String EXTRA_STOP_ID = "STOP_ID";
  private static final String EXTRA_STOP_NAME = "STOP_NAME";
  private static final String EXTRA_STOP_CODE = "STOP_CODE";
  private static final String EXTRA_LATITUDE = "LATITUDE";
  private static final String EXTRA_LONGITUDE = "LONGITUDE";

  public static Intent starterIntent(Context context, @NonNull StopPoint stopPoint) {
    return new Intent(context, DepartureActivity.class)
      .putExtra(EXTRA_STOP_ID, stopPoint.getStopId())
      .putExtra(EXTRA_STOP_NAME, stopPoint.getStopName())
      .putExtra(EXTRA_STOP_CODE, stopPoint.getStopCode())
      .putExtra(EXTRA_LATITUDE, stopPoint.getLatitude())
      .putExtra(EXTRA_LONGITUDE, stopPoint.getLongitude());
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_departure);

    if (savedInstanceState == null) {
      String stopId = getIntent().getStringExtra(EXTRA_STOP_ID);
      String stopName = getIntent().getStringExtra(EXTRA_STOP_NAME);
      String stopCode = getIntent().getStringExtra(EXTRA_STOP_CODE);
      double latitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0);
      double longitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0);

      getSupportFragmentManager().beginTransaction()
        .add(
          CONTAINER_ID,
          DepartureFragment.newInstance(stopId, stopName, stopCode, latitude, longitude)
        ).commit();
    }
  }
}
