package com.example.junyoung.culivebus.httpclient.pojos;

import java.util.ArrayList;

public class BusInfo {
  private String stopId;
  private String stopCode;
  private String stopName;
  private String headsign;
  private String tripHeadSign;
  private String routeColor;
  private String routeTextColor;
  private ArrayList<String> tripId = new ArrayList<>();
  private ArrayList<String> shapeId = new ArrayList<>();
  private ArrayList<String> expected = new ArrayList<>();
  private ArrayList<String> vehicleId = new ArrayList<>();

  public BusInfo(String stopId, String stopCode) {
    this.stopId = stopId;
    this.stopCode = stopCode;
  }

  public BusInfo(String headsign, String tripHeadSign, String tripId, String shapeId,
                 String vehicleId, String expected, String routeColor, String routeTextColor) {
    this.headsign = headsign;
    this.routeColor = routeColor;
    this.tripHeadSign = tripHeadSign;
    this.routeTextColor = routeTextColor;

    this.tripId.add(tripId);
    this.shapeId.add(shapeId);
    this.expected.add(expected);
    this.vehicleId.add(vehicleId);
  }

  public String getStopId() {
    return stopId;
  }

  public String getStopCode() {
    return stopCode;
  }

  public String getHeadsign() {
    return headsign;
  }

  public String getTripHeadSign() {
    return tripHeadSign;
  }

  public String getRouteColor() {
    return routeColor;
  }

  public String getRouteTextColor() {
    return routeTextColor;
  }

  public ArrayList<String> getTripId() {
    return tripId;
  }

  public ArrayList<String> getShapeId() {
    return shapeId;
  }

  public ArrayList<String> getExpected() {
    return expected;
  }

  public ArrayList<String> getVehicleId() {
    return vehicleId;
  }

  public void setBusInfo(String tripId, String shapeId, String expected, String vehicleId) {
    this.tripId.add(tripId);
    this.shapeId.add(shapeId);
    this.expected.add(expected);
    this.vehicleId.add(vehicleId);
  }

  public void setExpectedAtPosition(String expected, int position) {
    this.expected.set(position, expected);
  }

  public void deleteBusInfo(int index) {
    this.tripId.remove(index);
    this.shapeId.remove(index);
    this.expected.remove(index);
    this.vehicleId.remove(index);
  }
}
