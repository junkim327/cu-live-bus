package com.example.junyoung.culivebus.util;

import com.example.junyoung.culivebus.db.entity.FavoriteStop;
import com.example.junyoung.culivebus.db.entity.StopPoint;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {
  public static List<StopPoint> createBusStops(int count, String stopId, String stopCode,
                                               String stopName, double latitude,
                                               double longitude, boolean isRecentlySearched) {
    List<StopPoint> busStops = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      busStops.add(createBusStop(stopId + i, stopCode + i, stopName + i, latitude + i,
        longitude + i, isRecentlySearched));
    }

    return busStops;
  }

  public static StopPoint createBusStop(String stopId, String stopCode, String stopName,
                                        double latitude, double longitude,
                                        boolean isRecentlySearched) {
    return new StopPoint(stopId, stopCode, stopName, latitude, longitude, isRecentlySearched);
  }

  public static List<FavoriteStop> createFavoriteStops(int count, String stopId, String stopCode,
                                                       String stopName) {
    List<FavoriteStop> favoriteStops = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      favoriteStops.add(createFavoriteStop(stopId + i, stopCode + i, stopName + i));
    }

    return favoriteStops;
  }

  public static FavoriteStop createFavoriteStop(String stopId, String stopCode, String stopName) {
    return new FavoriteStop(stopId, stopCode, stopName);
  }
}
