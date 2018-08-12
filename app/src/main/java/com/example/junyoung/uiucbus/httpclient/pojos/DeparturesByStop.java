package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class DeparturesByStop {
  @Expose
  private Status status;
  @Expose
  private ArrayList<Departures> departures;

  public Status getStatus() {
    return status;
  }

  public ArrayList<Departures> getDepartures() {
    return departures;
  }
}
