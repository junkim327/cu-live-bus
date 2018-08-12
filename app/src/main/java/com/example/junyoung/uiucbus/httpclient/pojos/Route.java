package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.SerializedName;

public class Route {
  @SerializedName("route_color")
  private String routeColor;

  public String getRouteColor() {
    return routeColor;
  }
}
