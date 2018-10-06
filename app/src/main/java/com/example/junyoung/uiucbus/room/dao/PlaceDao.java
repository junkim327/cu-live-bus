package com.example.junyoung.uiucbus.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.junyoung.uiucbus.room.entity.UserPlace;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface PlaceDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public void insertPlace(UserPlace userPlace);

  @Query("DELETE FROM UserPlace WHERE uid = :userId")
  public void deleteAllPlacesByUid(String userId);

  @Query("SELECT * FROM UserPlace WHERE uid = :userId")
  public Flowable<List<UserPlace>> loadAllPlacesByUid(String userId);
}
