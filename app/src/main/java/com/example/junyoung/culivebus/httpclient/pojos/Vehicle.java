package com.example.junyoung.culivebus.httpclient.pojos;

import com.google.gson.annotations.SerializedName;

public class Vehicle {
  @SerializedName("trip")
  private Trip mTrip;
  @SerializedName("location")
  private Location mLocation;

  public Trip getTrip() {
    return mTrip;
  }

  public Location getLocation() {
    return mLocation;
  }
}
