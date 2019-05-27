package com.example.junyoung.culivebus.ui.direction.result;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class ItineraryBindingAdapter {
  @BindingAdapter(value = {"direction", "routeLongName", "routeShortName"})
  public static void setBusName(TextView view, String direction, String routeLongName,
                                String routeShortName) {
    StringBuilder builder = new StringBuilder(routeShortName);
    if (routeLongName.contains(" ")) {
      int lastIndex = routeLongName.indexOf(" ");
      routeLongName = routeLongName.substring(0, lastIndex);
    }
    String busName = builder.append(direction.charAt(0)).append(" ").append(routeLongName)
      .toString();
    view.setText(busName);
  }

  @BindingAdapter("colorFilter")
  public static void setColorFilter(ImageView view, String color) {
    if (color != null) {
      view.setColorFilter(Color.parseColor("#" + color), PorterDuff.Mode.SRC_IN);
    }
  }
}