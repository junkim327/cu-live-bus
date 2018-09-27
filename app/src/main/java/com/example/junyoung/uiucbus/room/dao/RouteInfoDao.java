package com.example.junyoung.uiucbus.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.junyoung.uiucbus.room.entity.RouteInfo;

import java.util.List;

@Dao
public interface RouteInfoDao {
  @Insert
  public void insertRouteInfo(RouteInfo routeInfo);

  @Delete
  public void deleteRouteInfos(RouteInfo routeInfo);

  @Query("SELECT * FROM route_info_table")
  public List<RouteInfo> loadAllRouteInfo();

  @Query("SELECT * FROM route_info_table WHERE uid = :userId")
  public RouteInfo[] loadAllRouteInfoByUid(String userId);
}
