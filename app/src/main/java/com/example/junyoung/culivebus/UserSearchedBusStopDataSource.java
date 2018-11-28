package com.example.junyoung.culivebus;

import com.example.junyoung.culivebus.room.entity.StopPoint;

import java.util.List;

import io.reactivex.Flowable;

public interface UserSearchedBusStopDataSource {
  void insertBusStop(StopPoint busStop);

  void deleteAllBusStopByUid(String uid);

  void deleteBusStopByUidAndStopId(String uid, String stopId);

  Flowable<List<StopPoint>> loadAllBusStopByUid(String uid);
}
