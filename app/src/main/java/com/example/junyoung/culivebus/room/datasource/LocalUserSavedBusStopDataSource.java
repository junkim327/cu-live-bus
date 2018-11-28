package com.example.junyoung.culivebus.room.datasource;

import com.example.junyoung.culivebus.UserSavedBusStopDataSource;
import com.example.junyoung.culivebus.room.dao.UserSavedBusStopDao;
import com.example.junyoung.culivebus.room.entity.UserSavedBusStop;

import java.util.List;

import io.reactivex.Flowable;

public class LocalUserSavedBusStopDataSource implements UserSavedBusStopDataSource {
  private final UserSavedBusStopDao mUserSavedBusStopDao;

  public LocalUserSavedBusStopDataSource(UserSavedBusStopDao userSavedBusStopDao) {
    mUserSavedBusStopDao = userSavedBusStopDao;
  }

  @Override
  public void insertBusStop(UserSavedBusStop busStop) {
    mUserSavedBusStopDao.insertBusStop(busStop);
  }

  @Override
  public void deleteAllBusStopByUid(String uid) {
    mUserSavedBusStopDao.deleteAllBusStopByUid(uid);
  }

  @Override
  public void deleteBusStopByUidAndStopId(String uid, String stopId) {
    mUserSavedBusStopDao.deleteBusStopByUidAndStopId(uid, stopId);
  }

  @Override
  public Flowable<List<UserSavedBusStop>> loadAllBusStopByUid(String uid) {
    return mUserSavedBusStopDao.loadAllBusStopByUid(uid);
  }

  @Override
  public Flowable<Integer> getNumBusStopsByUid(String uid, String stopId) {
    return mUserSavedBusStopDao.getNumBusStopsByUid(uid, stopId);
  }
}
