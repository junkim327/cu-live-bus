package com.example.junyoung.culivebus.room.datasource;

import com.example.junyoung.culivebus.UserSearchedBusStopDataSource;
import com.example.junyoung.culivebus.room.dao.UserSearchedBusStopDao;
import com.example.junyoung.culivebus.room.entity.StopPoint;

import java.util.List;

import io.reactivex.Flowable;

public class LocalUserSearchedBusStopDataSource implements UserSearchedBusStopDataSource {
  private final UserSearchedBusStopDao mUserSearchedBusStopDao;

  public LocalUserSearchedBusStopDataSource(UserSearchedBusStopDao userSearchedBusStopDao) {
    mUserSearchedBusStopDao = userSearchedBusStopDao;
  }

  @Override
  public void insertBusStop(StopPoint busStop) {
    mUserSearchedBusStopDao.insertBusStop(busStop);
  }

  @Override
  public void deleteAllBusStopByUid(String uid) {
    mUserSearchedBusStopDao.deleteAllBusStopByUid(uid);
  }

  @Override
  public void deleteBusStopByUidAndStopId(String uid, String stopId) {
    mUserSearchedBusStopDao.deleteBusStopByUidAndStopId(uid, stopId);
  }

  @Override
  public Flowable<List<StopPoint>> loadAllBusStopByUid(String uid) {
    return mUserSearchedBusStopDao.loadAllBusStopByUid(uid);
  }
}
