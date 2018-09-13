package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.SerializedName;

public class Route {
  @SerializedName("route_color")
  private String routeColor;
  @SerializedName("route_long_name")
  private String routeLongName;
  @SerializedName("route_short_name")
  private String routeShortName;
  @SerializedName("route_text_color")
  private String routeTextColor;

  public String getRouteColor() {
    return routeColor;
  }

  public String getRouteLongName() {
    return routeLongName;
  }

  public String getRouteShortName() {
    return routeShortName;
  }

  public String getRouteTextColor() {
    return routeTextColor;
  }
}
