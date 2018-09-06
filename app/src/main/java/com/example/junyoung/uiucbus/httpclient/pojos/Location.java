package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.Expose;

public class Location {
  @Expose
  private double lat;
  @Expose
  private double lon;

  public double getLat() {
    return lat;
  }

  public double getLon() {
    return lon;
  }
}
