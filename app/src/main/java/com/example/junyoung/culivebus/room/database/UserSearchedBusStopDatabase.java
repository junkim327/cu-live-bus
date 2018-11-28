package com.example.junyoung.culivebus.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.junyoung.culivebus.room.dao.UserSearchedBusStopDao;
import com.example.junyoung.culivebus.room.entity.StopPoint;

@Database(entities = {StopPoint.class}, version = 1)
public abstract class UserSearchedBusStopDatabase extends RoomDatabase {
  private static volatile UserSearchedBusStopDatabase INSTANCE;

  public abstract UserSearchedBusStopDao getUserSearchedBusStopDao();

  public static UserSearchedBusStopDatabase getUserSearchedBusStopDatabase(final Context context) {
    if (INSTANCE == null) {
      synchronized (StopPoint.class) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
            UserSearchedBusStopDatabase.class, "user_searched_bus_stop_database")
            .build();
        }
      }
    }

    return INSTANCE;
  }
}
