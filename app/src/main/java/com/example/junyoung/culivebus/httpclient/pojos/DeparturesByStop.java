package com.example.junyoung.culivebus.httpclient.pojos;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class DeparturesByStop {
  @Expose
  private Status status;
  @Expose
  private ArrayList<Departure> departures;

  public DeparturesByStop(Status status) {
    this.status = status;
  }

  public Status getStatus() {
    return status;
  }

  public ArrayList<Departure> getDepartures() {
    return departures;
  }
}
