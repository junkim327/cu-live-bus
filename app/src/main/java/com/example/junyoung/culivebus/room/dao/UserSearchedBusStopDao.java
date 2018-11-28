package com.example.junyoung.culivebus.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.junyoung.culivebus.room.entity.StopPoint;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserSearchedBusStopDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public void insertBusStop(StopPoint busStop);

  @Query("DELETE FROM user_searched_bus_stop_table WHERE uid = :userId")
  public void deleteAllBusStopByUid(String userId);

  @Query("DELETE FROM user_searched_bus_stop_table WHERE uid = :userId AND stop_id = :busStopId")
  public void deleteBusStopByUidAndStopId(String userId, String busStopId);

  @Query("SELECT * FROM user_searched_bus_stop_table WHERE uid = :userId")
  public Flowable<List<StopPoint>> loadAllBusStopByUid(String userId);
}
