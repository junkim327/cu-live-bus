package com.example.junyoung.uiucbus.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.junyoung.uiucbus.room.dao.RouteInfoDao;
import com.example.junyoung.uiucbus.room.entity.RouteInfo;

@Database(entities = {RouteInfo.class}, version = 1)
public abstract class RouteInfoDatabase extends RoomDatabase {
  public abstract RouteInfoDao getRouteInfoDao();
}
