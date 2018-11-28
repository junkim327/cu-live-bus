package com.example.junyoung.culivebus.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.junyoung.culivebus.room.dao.UserSavedBusStopDao;
import com.example.junyoung.culivebus.room.entity.UserSavedBusStop;

@Database(entities = {UserSavedBusStop.class}, version = 1)
public abstract class UserSavedBusStopDatabase extends RoomDatabase {
  private static volatile UserSavedBusStopDatabase INSTANCE;

  public abstract UserSavedBusStopDao getUserSavedBusStopDao();

  public static UserSavedBusStopDatabase getUserSavedBusStopDatabase(final Context context) {
    if (INSTANCE == null) {
      synchronized (UserSavedBusStopDatabase.class) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
            UserSavedBusStopDatabase.class, "user_saved_bus_stop_database")
            .build();
        }
      }
    }

    return INSTANCE;
  }
}
