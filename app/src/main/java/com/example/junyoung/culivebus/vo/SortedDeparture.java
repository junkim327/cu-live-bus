package com.example.junyoung.culivebus.vo;

import com.example.junyoung.culivebus.httpclient.pojos.Departure;
import com.example.junyoung.culivebus.httpclient.pojos.Route;
import com.example.junyoung.culivebus.httpclient.pojos.Trip;

import java.util.ArrayList;
import java.util.List;

public class SortedDeparture {
  private int numBus;
  private String headSign;
  private List<Trip> mTripList = new ArrayList<>();
  private List<Route> mRouteList = new ArrayList<>();
  private List<String> mExpectedList = new ArrayList<>();
  private List<String> mVehicleIdList = new ArrayList<>();

  public SortedDeparture(Departure departure) {
    numBus = 1;
    headSign = departure.getHeadsign();
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
    return headSign;
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
