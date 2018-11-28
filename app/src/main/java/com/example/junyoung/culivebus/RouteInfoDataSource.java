package com.example.junyoung.culivebus;

import com.example.junyoung.culivebus.room.entity.RouteInfo;

import java.util.List;

import io.reactivex.Flowable;

public interface RouteInfoDataSource {

  void insertRouteInfo(RouteInfo routeInfo);

  void deleteAllRouteInfoByUid(String uid);

  void deleteAllRouteInfo();

  Flowable<List<RouteInfo>> getAllRouteInfo();

  Flowable<List<RouteInfo>> getAllRouteInfoByUid(String uid);
}
