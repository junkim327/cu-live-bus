package com.example.junyoung.culivebus.db;

import com.example.junyoung.culivebus.db.dao.BusStopDao;
import com.example.junyoung.culivebus.vo.StopPoint2;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StopPoint2.class}, version = 1)
public abstract class CuLiveBusDb extends RoomDatabase {
  abstract public BusStopDao busStopDao();
}
