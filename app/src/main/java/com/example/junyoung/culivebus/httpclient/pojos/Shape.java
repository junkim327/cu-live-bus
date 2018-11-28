package com.example.junyoung.culivebus.httpclient.pojos;

import com.google.gson.annotations.SerializedName;

public class Shape {
  @SerializedName("shape_pt_lat")
  private double mShapeLat;
  @SerializedName("shape_pt_lon")
  private double mShapeLon;
  @SerializedName("stop_id")
  private String mStopId;


  public double getShapeLat() {
    return mShapeLat;
  }

  public double getShapeLon() {
    return mShapeLon;
  }

  public String getStopId() {
    return mStopId;
  }
}
