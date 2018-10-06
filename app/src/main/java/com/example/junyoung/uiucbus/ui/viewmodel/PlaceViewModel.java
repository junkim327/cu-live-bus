package com.example.junyoung.uiucbus.ui.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.example.junyoung.uiucbus.PlaceDataSource;
import com.example.junyoung.uiucbus.room.entity.UserPlace;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class PlaceViewModel extends ViewModel {
  private final PlaceDataSource mPlaceDataSource;

  private List<UserPlace> mUserPlaceList;

  public PlaceViewModel(PlaceDataSource dataSource) {
    mPlaceDataSource = dataSource;
  }

  public Flowable<List<UserPlace>> loadAllPlacesByUid(String uid) {
    return mPlaceDataSource.loadAllPlacesByUid(uid)
      .map(places -> {
        mUserPlaceList = places;
        return places;
      });
  }

  public Completable deleteAllPlacesByUid(String uid) {
    return Completable.fromAction(() -> {
      mUserPlaceList = null;
      mPlaceDataSource.deleteAllPlacesByUid(uid);
    });
  }

  public Completable insertPlace(final String uid, final double latitude, final double longitude,
                                 final String placeName) {
    return Completable.fromAction(() -> {
      UserPlace userPlace = new UserPlace(uid, latitude, longitude, placeName);
      mPlaceDataSource.insertPlace(userPlace);
    });
  }
}
