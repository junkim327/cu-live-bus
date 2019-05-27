package com.example.junyoung.culivebus.vo.response;

import com.example.junyoung.culivebus.vo.StopTime;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StopTimesResponse {
  @SerializedName("changeset_id")
  private String changeSetId;
  @SerializedName("stop_times")
  private List<StopTime> stopTimes;

  public String getChangeSetId() {
    return changeSetId;
  }

  public List<StopTime> getStopTimes() {
    return stopTimes;
  }
}
