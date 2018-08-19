package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.SerializedName;

public class Trip {
  @SerializedName("trip_id")
  private String tripId;
  @SerializedName("trip_headsign")
  private String tripHeadSign;

  public String getTripId() {
    return tripId;
  }

  public String getTripHeadSign() {
    return tripHeadSign;
  }
}
