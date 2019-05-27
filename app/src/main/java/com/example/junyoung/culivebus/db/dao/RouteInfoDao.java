package com.example.junyoung.culivebus.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.junyoung.culivebus.db.entity.RouteInfo;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface RouteInfoDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public void insertRouteInfo(RouteInfo routeInfo);

  @Insert
  public void insertAll(List<RouteInfo> routeInfos);

  @Query("DELETE FROM routeInfoTable")
  public void deleteAllRouteInfo();

  @Query("DELETE FROM routeInfoTable WHERE startingPointId = :startingId AND destinationId =:destId")
  public void deleteRouteInfo(String startingId, String destId);

  @Query("SELECT * FROM routeInfoTable")
  public Flowable<List<RouteInfo>> loadAllRouteInfo();
}
