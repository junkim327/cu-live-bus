package com.example.junyoung.culivebus.ui.departure;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.vo.SortedDeparture;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.vo.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import androidx.databinding.BindingAdapter;
import timber.log.Timber;

public class DepartureBindingAdapter {
  @BindingAdapter(value = {"stopIdList", "stopId"})
  public static void setClickable(ToggleButton toggleButton, List<String> stopIdList,
                                  String stopId) {
    if (stopIdList != null && stopId != null) {
      boolean isContained = stopIdList.contains(stopId);
      if (isContained || stopIdList.size() < 3) {
        toggleButton.setClickable(true);
      } else {
        toggleButton.setClickable(false);
      }
    }
  }

  @BindingAdapter(value = {"departuresResource", "mapLoaded"})
  public static void showHideNoDeparturesLayout(LinearLayout layout,
                                                Resource<List<SortedDeparture>> resource,
                                                Boolean isMapLoaded) {
    Timber.d("departuresResource");
    if (resource != null) {
      if (resource.status == Status.ERROR && resource.message.contentEquals("empty")
        && isMapLoaded) {
        layout.setVisibility(View.VISIBLE);
      } else {
        layout.setVisibility(View.GONE);
      }
    }
  }

  @BindingAdapter(value = {"markerLat", "markerLon"})
  public static void mapMarkers(MapView mapView, Double markerLat, Double markerLon) {
    Timber.d("mapMarker");
    if (markerLat != null && markerLon != null) {
      mapView.getMapAsync(googleMap -> {
        LatLng markerPosition = new LatLng(markerLat, markerLon);
        googleMap.addMarker(new MarkerOptions().position(markerPosition)
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.png_bus_stop_color)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerPosition));
      });
    }
  }

  @BindingAdapter("isMapToolbarEnabled")
  public static void isMapToolbarEnabled(MapView mapView, Boolean isMapToolbarEnabled) {
    if (isMapToolbarEnabled != null) {
      mapView.getMapAsync(googleMap ->
        googleMap.getUiSettings().setMapToolbarEnabled(isMapToolbarEnabled)
      );
    }
  }
}