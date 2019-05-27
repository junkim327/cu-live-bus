package com.example.junyoung.culivebus.db.dao;

import com.example.junyoung.culivebus.db.entity.FavoriteStop;

import org.jetbrains.annotations.TestOnly;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface FavoriteStopDao {
  @TestOnly
  @Insert
  void insertAll(List<FavoriteStop> favoriteStops);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertFavoriteStop(FavoriteStop favoriteStop);

  @Query("SELECT COUNT(*) FROM favoriteStopTable")
  LiveData<Integer> countFavoriteStops();

  @Query("SELECT * FROM favoriteStopTable")
  LiveData<List<FavoriteStop>> loadAllFavoriteStops();

  @Query("SELECT stopId FROM favoriteStopTable")
  LiveData<List<String>> loadStopIdList();

  @Query("SELECT stopId FROM favoriteStopTable")
  List<String> loadStopIdList2();

  @Query("DELETE FROM favoriteStopTable WHERE stopId = :busStopId")
  void deleteFavoriteStopByStopId(String busStopId);
}
