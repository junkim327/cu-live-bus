package com.example.junyoung.culivebus.httpclient.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StopTimesByTrip {
  @SerializedName("changeset_id")
  private String changeSetId;
  @SerializedName("stop_times")
  private ArrayList<StopTimes> stopTimes;

  public String getChangeSetId() {
    return changeSetId;
  }

  public ArrayList<StopTimes> getStopTimes() {
    return stopTimes;
  }
}
