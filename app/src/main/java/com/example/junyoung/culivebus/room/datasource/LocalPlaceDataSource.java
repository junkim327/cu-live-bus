package com.example.junyoung.culivebus.room.datasource;

import com.example.junyoung.culivebus.PlaceDataSource;
import com.example.junyoung.culivebus.room.dao.PlaceDao;
import com.example.junyoung.culivebus.room.entity.UserPlace;

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
