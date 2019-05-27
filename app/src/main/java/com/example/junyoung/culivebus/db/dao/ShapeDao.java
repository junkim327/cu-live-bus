package com.example.junyoung.culivebus.db.dao;

import com.example.junyoung.culivebus.db.entity.Shape;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;

@Dao
public interface ShapeDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<Shape> shapeList);

  @Query("SELECT * FROM shapeTable WHERE shapeId = :shapeId")
  LiveData<List<Shape>> loadShape(String shapeId);
}
