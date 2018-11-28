package com.example.junyoung.culivebus.ui.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.example.junyoung.culivebus.RouteInfoDataSource;
import com.example.junyoung.culivebus.room.entity.RouteInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class DirectionInfoViewModel extends ViewModel {
  private final RouteInfoDataSource mRouteInfoDataSource;

  private List<RouteInfo> mRouteInfoList;

  public DirectionInfoViewModel(RouteInfoDataSource dataSource) {
    mRouteInfoDataSource = dataSource;
  }

  public Flowable<List<RouteInfo>> getAllRouteInfo() {
    return mRouteInfoDataSource.getAllRouteInfo()
      .map(routeInfos -> {
        mRouteInfoList = routeInfos;
        return routeInfos;
      });
  }

  public Flowable<List<RouteInfo>> getRouteInfoListByUid(String uid) {
    return mRouteInfoDataSource.getAllRouteInfoByUid(uid)
      .map(routeInfos -> {
        mRouteInfoList = routeInfos;
        return mRouteInfoList;
      });
  }

  public Completable deleteAllRouteInfo() {
    return Completable.fromAction(() -> {
      mRouteInfoList = null;

      mRouteInfoDataSource.deleteAllRouteInfo();
    });
  }

  public Completable insertRouteInfo(final String uid, final String originLat,
                                     final String originLon, final String startingPointName,
                                     final String destinationLat, final String destinationLon,
                                     final String destinationName) {
    return Completable.fromAction(() -> {
      RouteInfo routeInfo =
        new RouteInfo(uid, originLat, originLon, startingPointName,
          destinationLat, destinationLon, destinationName);

      mRouteInfoDataSource.insertRouteInfo(routeInfo);
    });
  }
}
