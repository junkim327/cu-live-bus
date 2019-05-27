package com.example.junyoung.culivebus.httpclient.pojos;

import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Stop {
  @SerializedName("stop_id")
  private String stopId;
  @SerializedName("stop_name")
  private String stopName;
  @SerializedName("stop_points")
  private List<StopPoint> stopPoints;

  public String getStopId() {
    return stopId;
  }

  public String getStopName() {
    return stopName;
  }

  public List<StopPoint> getStopPoints() {
    return stopPoints;
  }
}
