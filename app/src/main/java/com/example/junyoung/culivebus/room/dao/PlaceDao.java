package com.example.junyoung.culivebus.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.junyoung.culivebus.room.entity.UserPlace;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface PlaceDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public void insertPlace(UserPlace userPlace);

  @Query("DELETE FROM place_table WHERE uid = :userId")
  public void deleteAllPlacesByUid(String userId);

  @Query("SELECT * FROM place_table WHERE uid = :userId")
  public Flowable<List<UserPlace>> loadAllPlacesByUid(String userId);
}
