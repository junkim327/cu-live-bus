package com.example.junyoung.culivebus.db.dao;

import com.example.junyoung.culivebus.db.entity.SearchedPlace;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SearchedPlaceDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertPlace(SearchedPlace searchedPlace);

  @Query("SELECT * FROM placeTable ORDER BY rowId DESC LIMIT 7")
  LiveData<List<SearchedPlace>> loadSevenSearchedPlaces();

  @Query("SELECT * FROM placeTable ORDER BY rowId DESC")
  LiveData<List<SearchedPlace>> loadAllSearchedPlaces();
}

