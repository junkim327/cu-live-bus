package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.Expose;

public class Vehicle {
  @Expose
  private Location location;

  public Location getLocation() {
    return location;
  }
}
