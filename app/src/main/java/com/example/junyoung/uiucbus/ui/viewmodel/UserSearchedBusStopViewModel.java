package com.example.junyoung.uiucbus.ui.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.example.junyoung.uiucbus.UserSearchedBusStopDataSource;
import com.example.junyoung.uiucbus.room.entity.UserSearchedBusStop;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class UserSearchedBusStopViewModel extends ViewModel {
  private final UserSearchedBusStopDataSource mUserSearchedBusStopDataSource;

  private List<UserSearchedBusStop> mUserSearchedBusStopList = new ArrayList<>();

  public UserSearchedBusStopViewModel(UserSearchedBusStopDataSource userSearchedBusStopDataSource) {
    mUserSearchedBusStopDataSource = userSearchedBusStopDataSource;
  }

  public Completable insertBusStop(final String uid, final String stopId,
                                   final String stopCode, final String stopName,
                                   final double latitude, final double longitude) {
    return Completable.fromAction(() -> {
      UserSearchedBusStop busStop =
        new UserSearchedBusStop(uid, stopId, stopCode, stopName, latitude, longitude);

      mUserSearchedBusStopDataSource.insertBusStop(busStop);
    });
  }

  public Completable deleteAllBusStopsByUid(String uid) {
    return Completable.fromAction(() -> {
      mUserSearchedBusStopList.clear();

      mUserSearchedBusStopDataSource.deleteAllBusStopByUid(uid);
    });
  }

  public Completable deleteBusStopByUidAndStopId(String uid, String stopId) {
    return Completable.fromAction(() -> {
      mUserSearchedBusStopDataSource.deleteBusStopByUidAndStopId(uid, stopId);
    });
  }

  public Flowable<List<UserSearchedBusStop>> loadAllBusStopsByUid(String uid) {
    return mUserSearchedBusStopDataSource.loadAllBusStopByUid(uid)
      .map(userSearchedBusStops -> {
        mUserSearchedBusStopList.clear();
        mUserSearchedBusStopList.addAll(userSearchedBusStops);

        return mUserSearchedBusStopList;
      });
  }
}
