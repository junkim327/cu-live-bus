package com.example.junyoung.uiucbus.room.datasource;

import com.example.junyoung.uiucbus.UserSearchedBusStopDataSource;
import com.example.junyoung.uiucbus.room.dao.UserSearchedBusStopDao;
import com.example.junyoung.uiucbus.room.entity.UserSearchedBusStop;

import java.util.List;

import io.reactivex.Flowable;

public class LocalUserSearchedBusStopDataSource implements UserSearchedBusStopDataSource {
  private final UserSearchedBusStopDao mUserSearchedBusStopDao;

  public LocalUserSearchedBusStopDataSource(UserSearchedBusStopDao userSearchedBusStopDao) {
    mUserSearchedBusStopDao = userSearchedBusStopDao;
  }

  @Override
  public void insertBusStop(UserSearchedBusStop busStop) {
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
  public Flowable<List<UserSearchedBusStop>> loadAllBusStopByUid(String uid) {
    return mUserSearchedBusStopDao.loadAllBusStopByUid(uid);
  }
}
