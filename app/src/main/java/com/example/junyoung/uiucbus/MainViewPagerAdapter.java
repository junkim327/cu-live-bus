package com.example.junyoung.uiucbus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainViewPagerAdapter extends PagerAdapter {
  private String[] headerList;

  private Context context;

  public MainViewPagerAdapter(Context context) {
    this.context = context;
    headerList = new String[]{"Nearby stops", "Direct bus to home", "Nearby night buses"};
  }

  @Override
  public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    container.removeView((View) object);
  }

  @Override
  public int getCount() {
    return 3;
  }

  @Override
  public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
    return view == object;
  }

  @NonNull
  @Override
  public Object instantiateItem(@NonNull ViewGroup container, final int position) {
    LayoutInflater inflater = LayoutInflater.from(context);
    ViewGroup view = (ViewGroup) inflater.inflate(R.layout.main_view_item, container, false);

    final TextView header = view.findViewById(R.id.text_header_main_view_item);
    header.setText(headerList[position]);

    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (position == 0) {
          if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
              new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
              45);
          } else {
            FusedLocationProviderClient fusedLocationProviderClient;
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationProviderClient.getLastLocation()
              .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                  Intent intent = new Intent(context, BusStopsInMapActivity.class);
                  intent.putExtra("latitude", String.valueOf(location.getLatitude()));
                  intent.putExtra("longitude", String.valueOf(location.getLongitude()));
                  context.startActivity(intent);
                }
              });
          }
        }
      }
    });

    container.addView(view);

    return view;
  }
}
