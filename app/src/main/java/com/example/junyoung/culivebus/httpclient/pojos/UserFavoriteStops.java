package com.example.junyoung.culivebus.httpclient.pojos;

import java.util.ArrayList;

public class UserFavoriteStops {
  private ArrayList<String> busStopIds;
  private ArrayList<String> busStopCodes;
  private ArrayList<String> busStopNames;

  public UserFavoriteStops(ArrayList<String> busStopIds,
                           ArrayList<String> busStopCodes,
                           ArrayList<String> busStopNames) {
    this.busStopIds = busStopIds;
    this.busStopCodes = busStopCodes;
    this.busStopNames = busStopNames;
  }

  public ArrayList<String> getBusStopIds() {
    return busStopIds;
  }

  public ArrayList<String> getBusStopCodes() {
    return busStopCodes;
  }

  public ArrayList<String> getBusStopNames() {
    return busStopNames;
  }
}
