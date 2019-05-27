package com.example.junyoung.culivebus.db.dao;

import com.example.junyoung.culivebus.db.entity.Changeset;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ChangesetDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertChangeset(Changeset changeset);

  @Query("SELECT changesetId FROM changesetTable WHERE keyword = :keyword")
  LiveData<String> loadChangeset(String keyword);
}
