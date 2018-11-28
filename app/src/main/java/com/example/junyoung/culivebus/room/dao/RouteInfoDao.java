package com.example.junyoung.culivebus.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.junyoung.culivebus.room.entity.RouteInfo;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface RouteInfoDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public void insertRouteInfo(RouteInfo routeInfo);

  @Insert
  public void insertAll(List<RouteInfo> routeInfos);

  @Query("DELETE FROM route_info_table")
  public void deleteAllRouteInfo();

  @Query("DELETE FROM route_info_table WHERE uid = :userId")
  public void deleteAllRouteInfoByUid(String userId);

  @Query("SELECT * FROM route_info_table")
  public Flowable<List<RouteInfo>> loadAllRouteInfo();

  @Query("SELECT * FROM route_info_table WHERE uid = :userId")
  public Flowable<List<RouteInfo>> loadAllRouteInfoByUid(String userId);
}
