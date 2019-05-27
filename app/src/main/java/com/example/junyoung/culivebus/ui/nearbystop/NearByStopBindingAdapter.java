package com.example.junyoung.culivebus.ui.nearbystop;

import android.content.Context;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.example.junyoung.culivebus.util.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import androidx.databinding.BindingAdapter;
import timber.log.Timber;

public class NearByStopBindingAdapter {
  @BindingAdapter("latlng")
  public static void animateCamera(MapView mapView, LatLng latLng) {
    if (latLng != null) {
      mapView.getMapAsync(googleMap -> {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
      });
    }
  }

  @BindingAdapter("mapPaddingBottom")
  public static void setupMapPadding(MapView mapView, Integer paddingBottom) {
    if (paddingBottom != null) {
      mapView.getMapAsync(googleMap -> {
        googleMap.setPadding(0, 0, 0, paddingBottom);
      });
    }
  }

  @BindingAdapter("clusterClickEvent")
  public static void onClusterClick(MapView mapView, Event<LatLng> clusterClickEvent) {
    if (clusterClickEvent != null) {
      LatLng clusterLatLng = clusterClickEvent.getContentIfNotHandled();
      if (clusterLatLng == null) {
        return;
      }
      mapView.getMapAsync(googleMap -> {
        googleMap.animateCamera(
          CameraUpdateFactory.newLatLngZoom(
            clusterLatLng,
            googleMap.getCameraPosition().zoom + 1
          )
        );
      });
    }
  }
}
