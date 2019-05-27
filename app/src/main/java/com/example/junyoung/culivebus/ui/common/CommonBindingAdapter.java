package com.example.junyoung.culivebus.ui.common;

import android.view.View;

import com.example.junyoung.culivebus.util.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.databinding.BindingAdapter;
import timber.log.Timber;

public class CommonBindingAdapter {
  @BindingAdapter("bottomSheetState")
  public static void setBottomSheetState(View view, Event<Integer> event) {
    if (event != null) {
      Integer boxedState = event.getContentIfNotHandled();
      if (boxedState == null) {
        return;
      }
      int state = boxedState;
      BottomSheetBehavior.from(view).setState(state);
    }
  }

  @BindingAdapter(value = {"customLatLng1", "customZoom1", "shouldAnimate1"})
  public static void moveOrAnimateCamera(MapView mapView, LatLng latLng,
                                         Float zoom, Boolean shouldAnimate) {
    Timber.d("Binding Adapter has been called.");
    Timber.d("latlng : %s, zoom : %s, shouldAnimate : %s", latLng, zoom, shouldAnimate);
    if (latLng != null && zoom != null && shouldAnimate != null) {
      mapView.getMapAsync(googleMap -> {
        Timber.d("latlng : %s, zoom : %s, shouldAnimate : %s", latLng, zoom, shouldAnimate);
        boolean isZoomValueSame = googleMap.getCameraPosition().zoom == zoom;

        if (shouldAnimate) {
          if (isZoomValueSame) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), 500, null);
          } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 500, null);
          }
        } else {
          if (isZoomValueSame) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
          } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
          }
        }
      });
    }
  }
}
