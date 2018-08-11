package com.example.junyoung.uiucbus.httpclient.objects;

import com.google.gson.annotations.SerializedName;

public class StopPoint {
  @SerializedName("stop_lat")
  private double stopLat;
  @SerializedName("stop_lon")
  private double stopLon;
  @SerializedName("stop_name")
  private String stopName;

  public double getStopLat() {
    return stopLat;
  }

  public double getStopLon() {
    return stopLon;
  }

  public String getStopName() {
    return stopName;
  }
}
