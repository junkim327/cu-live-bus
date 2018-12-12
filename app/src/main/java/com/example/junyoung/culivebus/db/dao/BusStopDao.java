package com.example.junyoung.culivebus.db.dao;

import com.example.junyoung.culivebus.vo.StopPoint2;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface BusStopDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<StopPoint2> stopPoints);

  @Query("SELECT COUNT(*) FROM busStopTable")
  LiveData<Integer> countBusStops();
}
