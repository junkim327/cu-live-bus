package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class PlannedTrips {
  @Expose
  private ArrayList<Itinerary> itineraries = null;

  public ArrayList<Itinerary> getItineraries() {
    return itineraries;
  }
}
