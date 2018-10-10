package com.example.junyoung.uiucbus.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.junyoung.uiucbus.room.entity.UserSavedBusStop;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserSavedBusStopDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public void insertBusStop(UserSavedBusStop busStop);

  @Query("DELETE FROM user_saved_bus_stop_table WHERE uid = :userId")
  public void deleteAllBusStopByUid(String userId);

  @Query("DELETE FROM user_saved_bus_stop_table WHERE uid = :userId AND saved_stop_id = :busStopId")
  public void deleteBusStopByUidAndStopId(String userId, String busStopId);

  @Query("SELECT * FROM user_saved_bus_stop_table WHERE uid = :userId")
  public Flowable<List<UserSavedBusStop>> loadAllBusStopByUid(String userId);
}
