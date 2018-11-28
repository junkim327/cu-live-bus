package com.example.junyoung.culivebus.httpclient.pojos;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Departure implements Comparable, Cloneable {
  @Expose
  private Trip trip;
  @Expose
  private Route route;
  @Expose
  private String headsign;
  @Expose
  private String expected;
  @SerializedName("vehicle_id")
  private String vehicleId;

  public Trip getTrip() {
    return trip;
  }

  public Route getRoute() {
    return route;
  }

  public String getHeadsign() {
    return headsign;
  }

  public String getExpected() {
    return expected;
  }

  public String getVehicleId() {
    return vehicleId;
  }

  @Override
  public int compareTo(@NonNull Object o) {
    Departure compare = (Departure) o;
    if (compare.getExpected().equals(this.expected)) {
      return 0;
    }

    return 1;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    Departure clone;
    try {
      clone = (Departure) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }

    return clone;
  }
}
