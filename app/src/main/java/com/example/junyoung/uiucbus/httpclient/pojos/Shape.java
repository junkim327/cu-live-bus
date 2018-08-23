package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.SerializedName;

public class Shape {
  @SerializedName("shape_pt_lat")
  private double shapeLat;
  @SerializedName("shape_pt_lon")
  private double shapeLon;

  public double getShapeLat() {
    return shapeLat;
  }

  public double getShapeLon() {
    return shapeLon;
  }
}
