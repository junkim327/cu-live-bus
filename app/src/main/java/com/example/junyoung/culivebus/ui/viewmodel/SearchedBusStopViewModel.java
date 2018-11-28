package com.example.junyoung.culivebus.ui.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.example.junyoung.culivebus.UserSearchedBusStopDataSource;
import com.example.junyoung.culivebus.room.entity.StopPoint;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class SearchedBusStopViewModel extends ViewModel {
  private final UserSearchedBusStopDataSource mUserSearchedBusStopDataSource;

  private List<StopPoint> mUserSearchedBusStopList = new ArrayList<>();

  public SearchedBusStopViewModel(UserSearchedBusStopDataSource userSearchedBusStopDataSource) {
    mUserSearchedBusStopDataSource = userSearchedBusStopDataSource;
  }

  public Completable insertBusStop(final String uid, final String stopId,
                                   final String stopCode, final String stopName,
                                   final double latitude, final double longitude) {
    return Completable.fromAction(() -> {
      StopPoint busStop =
        new StopPoint(uid, stopId, stopCode, stopName, latitude, longitude);

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

  public Flowable<List<StopPoint>> loadAllBusStopsByUid(String uid) {
    return mUserSearchedBusStopDataSource.loadAllBusStopByUid(uid)
      .map(userSearchedBusStops -> {
        mUserSearchedBusStopList.clear();
        mUserSearchedBusStopList.addAll(userSearchedBusStops);

        return mUserSearchedBusStopList;
      });
  }
}
