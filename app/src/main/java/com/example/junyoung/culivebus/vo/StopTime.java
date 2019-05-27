package com.example.junyoung.culivebus.vo;

import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.google.gson.annotations.SerializedName;

public class StopTime {
  @SerializedName("stop_point")
  private StopPoint stopPoint;
  @SerializedName("stop_sequence")
  private String stopSequence;

  public StopPoint getStopPoint() {
    return stopPoint;
  }

  public String getStopSequence() {
    return stopSequence;
  }
}
