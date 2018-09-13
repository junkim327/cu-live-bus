package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.Expose;

public class Service {
  @Expose
  private Point end;
  @Expose
  private Trip trip;
  @Expose
  private Route route;
  @Expose
  private Point begin;

  public Point getEnd() {
    return end;
  }

  public Trip getTrip() {
    return trip;
  }

  public Route getRoute() {
    return route;
  }

  public Point getBegin() {
    return begin;
  }
}
