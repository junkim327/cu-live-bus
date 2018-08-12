package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.Expose;

public class Departures {
  @Expose
  private Route route;
  @Expose
  private String headsign;
  @Expose
  private String expected;

  public Route getRoute() {
    return route;
  }

  public String getHeadsign() {
    return headsign;
  }

  public String getExpected() {
    return expected;
  }
}
