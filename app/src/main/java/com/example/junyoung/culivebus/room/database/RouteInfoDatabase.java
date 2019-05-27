package com.example.junyoung.culivebus.room.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.junyoung.culivebus.db.dao.RouteInfoDao;
import com.example.junyoung.culivebus.db.entity.RouteInfo;

@Database(entities = {RouteInfo.class}, version = 1)
public abstract class RouteInfoDatabase extends RoomDatabase {
  private static volatile RouteInfoDatabase INSTANCE;

  public abstract RouteInfoDao getRouteInfoDao();

  public static RouteInfoDatabase getRouteInfoDatabase(final Context context) {
    if (INSTANCE == null) {
      synchronized (RouteInfoDatabase.class) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
            RouteInfoDatabase.class, "route_info_database")
            .build();
        }
      }
    }

    return INSTANCE;
  }


}
