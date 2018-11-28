package com.example.junyoung.culivebus;

import com.example.junyoung.culivebus.room.entity.UserSavedBusStop;

import java.util.List;

import io.reactivex.Flowable;

public interface UserSavedBusStopDataSource {
  void insertBusStop(UserSavedBusStop busStop);

  void deleteAllBusStopByUid(String uid);

  void deleteBusStopByUidAndStopId(String uid, String stopId);

  Flowable<List<UserSavedBusStop>> loadAllBusStopByUid(String uid);

  Flowable<Integer> getNumBusStopsByUid(String uid, String stopId);
}
