package com.example.junyoung.uiucbus.ui.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.example.junyoung.uiucbus.UserSavedBusStopDataSource;
import com.example.junyoung.uiucbus.room.entity.UserSavedBusStop;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class UserSavedBusStopViewModel extends ViewModel {
  private final UserSavedBusStopDataSource mUserSavedBusStopDataSource;

  private List<UserSavedBusStop> mUserSavedBusStops;

  public UserSavedBusStopViewModel(UserSavedBusStopDataSource userSavedBusStopDataSource) {
    mUserSavedBusStopDataSource = userSavedBusStopDataSource;
  }

  public Completable insertBusStop(final String uid, final String busStopId,
                                   final String busStopCode, final String busStopName) {
    return Completable.fromAction(() -> {
      UserSavedBusStop userSavedBusStop =
        new UserSavedBusStop(uid, busStopId, busStopCode, busStopName);

      mUserSavedBusStopDataSource.insertBusStop(userSavedBusStop);
    });
  }

  public Completable deleteAllBusStopsByUid(String uid) {
    return Completable.fromAction(() -> {
      mUserSavedBusStops = null;

      mUserSavedBusStopDataSource.deleteAllBusStopByUid(uid);
    });
  }


  public Completable deleteBusStopByUidAndStopId(String uid, String stopId) {
    return Completable.fromAction(() -> {
      mUserSavedBusStopDataSource.deleteBusStopByUidAndStopId(uid, stopId);
    });
  }

  public Flowable<List<UserSavedBusStop>> loadAllBusStopsByUid(String uid) {
    return mUserSavedBusStopDataSource.loadAllBusStopByUid(uid)
      .map(userSavedBusStops -> {
        mUserSavedBusStops = userSavedBusStops;
        return mUserSavedBusStops;
      });
  }
}
