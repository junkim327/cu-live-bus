package com.example.junyoung.uiucbus.httpclient.pojos;

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
