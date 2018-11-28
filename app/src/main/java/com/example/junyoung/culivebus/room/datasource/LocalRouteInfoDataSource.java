package com.example.junyoung.culivebus.room.datasource;

import com.example.junyoung.culivebus.RouteInfoDataSource;
import com.example.junyoung.culivebus.room.dao.RouteInfoDao;
import com.example.junyoung.culivebus.room.entity.RouteInfo;

import java.util.List;

import io.reactivex.Flowable;

public class LocalRouteInfoDataSource implements RouteInfoDataSource {
  private final RouteInfoDao mRouteInfoDao;

  public LocalRouteInfoDataSource(RouteInfoDao routeInfoDao) {
    mRouteInfoDao = routeInfoDao;
  }

  @Override
  public Flowable<List<RouteInfo>> getAllRouteInfo() {
    return mRouteInfoDao.loadAllRouteInfo();
  }

  @Override
  public Flowable<List<RouteInfo>> getAllRouteInfoByUid(String uid) {
    return mRouteInfoDao.loadAllRouteInfoByUid(uid);
  }

  @Override
  public void insertRouteInfo(RouteInfo routeInfo) {
    mRouteInfoDao.insertRouteInfo(routeInfo);
  }

  @Override
  public void deleteAllRouteInfoByUid(String uid) {
    mRouteInfoDao.deleteAllRouteInfoByUid(uid);
  }

  @Override
  public void deleteAllRouteInfo() {
    mRouteInfoDao.deleteAllRouteInfo();
  }
}
