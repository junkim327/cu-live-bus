package com.example.junyoung.uiucbus.room.datasource;

import com.example.junyoung.uiucbus.PlaceDataSource;
import com.example.junyoung.uiucbus.room.dao.PlaceDao;
import com.example.junyoung.uiucbus.room.entity.UserPlace;

import java.util.List;

import io.reactivex.Flowable;

public class LocalPlaceDataSource implements PlaceDataSource {
  private final PlaceDao mPlaceDao;

  public LocalPlaceDataSource(PlaceDao placeDao) {
    mPlaceDao = placeDao;
  }

  @Override
  public Flowable<List<UserPlace>> loadAllPlacesByUid(String uid) {
    return mPlaceDao.loadAllPlacesByUid(uid);
  }

  @Override
  public void insertPlace(UserPlace userPlace) {
    mPlaceDao.insertPlace(userPlace);
  }

  @Override
  public void deleteAllPlacesByUid(String uid) {
    mPlaceDao.deleteAllPlacesByUid(uid);
  }
}
