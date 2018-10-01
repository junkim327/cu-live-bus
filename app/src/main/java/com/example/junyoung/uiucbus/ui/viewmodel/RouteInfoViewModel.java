package com.example.junyoung.uiucbus.ui.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.example.junyoung.uiucbus.RouteInfoDataSource;
import com.example.junyoung.uiucbus.room.entity.RouteInfo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class RouteInfoViewModel extends ViewModel {
  private final RouteInfoDataSource mRouteInfoDataSource;

  private List<RouteInfo> routeInfoList;

  public RouteInfoViewModel(RouteInfoDataSource dataSource) {
    mRouteInfoDataSource = dataSource;
  }

  public Flowable<List<RouteInfo>> getAllRouteInfo() {
    return mRouteInfoDataSource.getAllRouteInfo()
      .map(routeInfos -> {
        routeInfoList = routeInfos;
        return routeInfos;
      });
  }

  public Flowable<List<RouteInfo>> getRouteInfoListByUid(String uid) {
    return mRouteInfoDataSource.getAllRouteInfoByUid(uid)
      .map(routeInfos -> {
        routeInfoList = routeInfos;
        return routeInfoList;
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
