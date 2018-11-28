package com.example.junyoung.culivebus.httpclient.pojos;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class BusStops {
  @Expose
  private Status status;
  @Expose
  private ArrayList<Stop> stops;

  public Status getStatus() {
    return status;
  }

  public ArrayList<Stop> getStops() {
    return stops;
  }
}
