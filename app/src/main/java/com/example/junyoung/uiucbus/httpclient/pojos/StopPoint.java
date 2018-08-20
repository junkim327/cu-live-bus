package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopPoint {
  @Expose
  private String code;
  @SerializedName("stop_id")
  private String stopId;
  @SerializedName("stop_lat")
  private double stopLat;
  @SerializedName("stop_lon")
  private double stopLon;
  @SerializedName("stop_name")
  private String stopName;

  public String getCode() {
    return code;
  }

  public String getStopId() {
    return stopId;
  }

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
