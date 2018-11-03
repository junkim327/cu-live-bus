package com.example.junyoung.uiucbus.httpclient.pojos;

import java.util.ArrayList;
import java.util.List;

public class SortedDeparture {
  private int numBus;
  private String mHeadSign;
  private List<Trip> mTripList = new ArrayList<>();
  private List<Route> mRouteList = new ArrayList<>();
  private List<String> mExpectedList = new ArrayList<>();
  private List<String> mVehicleIdList = new ArrayList<>();

  public SortedDeparture(Departure departure) {
    numBus = 1;
    mHeadSign = departure.getHeadsign();
    mTripList.add(departure.getTrip());
    mRouteList.add(departure.getRoute());
    mExpectedList.add(departure.getExpected());
    mVehicleIdList.add(departure.getVehicleId());
  }

  public void storeDepartureInfo(Departure departure) {
    if (numBus < 2) {
      numBus++;
      mTripList.add(departure.getTrip());
      mRouteList.add(departure.getRoute());
      mExpectedList.add(departure.getExpected());
      mVehicleIdList.add(departure.getVehicleId());
    }
  }

  public String getHeadSign() {
    return mHeadSign;
  }

  public List<Trip> getTripList() {
    return mTripList;
  }

  public List<Route> getRouteList() {
    return mRouteList;
  }

  public List<String> getExpectedList() {
    return mExpectedList;
  }

  public List<String> getVehicleIdList() {
    return mVehicleIdList;
  }
}
