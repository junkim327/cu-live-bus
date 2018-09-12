package com.example.junyoung.uiucbus.httpclient.pojos;

import java.util.ArrayList;

public class UserFavoriteBusSchedules {
  private ArrayList<BusSchedules> busSchedulesList;
  private ArrayList<DeparturesByStop> departuresByStopsList;

  public UserFavoriteBusSchedules(DeparturesByStop departures1, DeparturesByStop departures2) {
    departuresByStopsList = new ArrayList<>();
    departuresByStopsList.add(departures1);
    departuresByStopsList.add(departures2);
  }

  public UserFavoriteBusSchedules(BusSchedules busSchedules1) {
    busSchedulesList = new ArrayList<>();
    busSchedulesList.add(busSchedules1);
  }

  public ArrayList<DeparturesByStop> getDepartures() {
    return departuresByStopsList;
  }
}
