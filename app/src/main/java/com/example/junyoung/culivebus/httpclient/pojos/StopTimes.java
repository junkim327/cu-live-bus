package com.example.junyoung.culivebus.httpclient.pojos;

import com.example.junyoung.culivebus.room.entity.StopPoint;
import com.google.gson.annotations.SerializedName;

public class StopTimes {
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
