package com.example.junyoung.culivebus.httpclient.pojos;

import com.example.junyoung.culivebus.room.entity.StopPoint;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Stop {
  @SerializedName("stop_id")
  private String stopId;
  @SerializedName("stop_name")
  private String stopName;
  @Expose
  private double distance;
  @SerializedName("stop_points")
  private ArrayList<StopPoint> stopPoints;

  public String getStopId() {
    return stopId;
  }

  public String getStopName() {
    return stopName;
  }

  public Double getDistance() {
    return distance;
  }

  public ArrayList<StopPoint> getStopPoints() {
    return stopPoints;
  }
}
