package com.example.junyoung.uiucbus.httpclient.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Stop {
  @SerializedName("stop_id")
  private String stopId;
  @SerializedName("stop_name")
  private String stopName;
  @SerializedName("stop_points")
  private ArrayList<StopPoint> stopPoints;

  public String getStopId() {
    return stopId;
  }

  public String getStopName() {
    return stopName;
  }

  public ArrayList<StopPoint> getStopPoints() {
    return stopPoints;
  }
}
