package com.example.junyoung.culivebus.db.dao;

import com.example.junyoung.culivebus.db.entity.StopPoint;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface BusStopDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<StopPoint> stopPoints);

  @Query("SELECT * FROM busStopTable ORDER BY id")
  LiveData<List<StopPoint>> loadAllBusStops();

  @Query("SELECT COUNT(*) FROM busStopTable")
  LiveData<Integer> countBusStops();

  @Query("SELECT * FROM busStopTable WHERE isRecentSearched")
  LiveData<List<StopPoint>> loadRecentlySearchedBusStops();

  @Update
  void updateBusStop(StopPoint stopPoint);

  @Query("SELECT busStopTable.* FROM busStopTable JOIN busStopFtsTable ON (busStopTable.id " +
    "= busStopFtsTable.rowId) WHERE busStopFtsTable MATCH :query LIMIT 10")
  LiveData<List<StopPoint>> searchAllBusStops(String query);
}
