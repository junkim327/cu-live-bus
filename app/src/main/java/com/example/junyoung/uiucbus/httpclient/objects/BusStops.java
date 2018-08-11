package com.example.junyoung.uiucbus.httpclient.objects;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class BusStops {
  @Expose
  private ArrayList<Stop> stops;

  public ArrayList<Stop> getStops() {
    return stops;
  }
}
