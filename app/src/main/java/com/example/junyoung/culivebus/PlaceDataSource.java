package com.example.junyoung.culivebus;

import com.example.junyoung.culivebus.room.entity.UserPlace;

import java.util.List;

import io.reactivex.Flowable;

public interface PlaceDataSource {
  Flowable<List<UserPlace>> loadAllPlacesByUid(String uid);

  void insertPlace(UserPlace userPlace);

  void deleteAllPlacesByUid(String uid);
}
