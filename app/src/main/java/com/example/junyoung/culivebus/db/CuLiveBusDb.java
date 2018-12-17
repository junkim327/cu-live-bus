package com.example.junyoung.culivebus.db;

import com.example.junyoung.culivebus.db.dao.BusStopDao;
import com.example.junyoung.culivebus.db.entity.StopPointFts;
import com.example.junyoung.culivebus.db.entity.StopPoint;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StopPoint.class, StopPointFts.class}, version = 1)
public abstract class CuLiveBusDb extends RoomDatabase {
  abstract public BusStopDao busStopDao();
}
