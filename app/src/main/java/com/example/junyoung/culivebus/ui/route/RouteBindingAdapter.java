package com.example.junyoung.culivebus.ui.route;

import android.graphics.Color;
import android.util.TypedValue;

import com.example.junyoung.culivebus.db.entity.Shape;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.List;

import androidx.annotation.DimenRes;
import androidx.databinding.BindingAdapter;

public class RouteBindingAdapter {
  @BindingAdapter(value = {"shapeList", "routeColor"})
  public static void drawPolyline(MapView mapView, List<Shape> shapeList, String routeColor) {
    if (shapeList != null) {
      mapView.getMapAsync(googleMap -> {
        PolylineOptions polylineOptions = new PolylineOptions();
        for (Shape shape : shapeList) {
          LatLng shapeLatLng = new LatLng(shape.getShapeLat(), shape.getShapeLon());
          polylineOptions.add(shapeLatLng);
        }

        polylineOptions
          .width(20)
          .geodesic(true);

        if (routeColor != null) {
          polylineOptions.color(Color.parseColor(routeColor));
        }

        Polyline polyline = googleMap.addPolyline(polylineOptions);
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setJointType(JointType.ROUND);
      });
    }
  }

  @BindingAdapter("minZoomPreference")
  public static void setMinZoomPreference(MapView mapView, @DimenRes int resId) {
    TypedValue outValue = new TypedValue();
    mapView.getResources().getValue(resId, outValue, true);
    float minZoom = outValue.getFloat();
    mapView.getMapAsync(googleMap -> {
      googleMap.setMinZoomPreference(minZoom);
    });
  }
}
