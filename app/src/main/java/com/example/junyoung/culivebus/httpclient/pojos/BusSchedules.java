package com.example.junyoung.culivebus.httpclient.pojos;

import java.util.ArrayList;

public class BusSchedules {
  private ArrayList<String> busNameList;
  private ArrayList<BusInfo> busInfoList;

  public BusSchedules() {
    busNameList = new ArrayList<>();
    busInfoList = new ArrayList<>();
  }

  public ArrayList<String> getBusNameList() {
    return busNameList;
  }

  public ArrayList<BusInfo> getBusInfoList() {
    return busInfoList;
  }

  public void addBusName(String busName) {
    this.busNameList.add(busName);
  }

  public void addBusInfo(BusInfo busInfo) {
    this.busInfoList.add(busInfo);
  }

  public void addBusStopInfo(BusInfo busInfo) { this.busInfoList.add(busInfo); }
}
