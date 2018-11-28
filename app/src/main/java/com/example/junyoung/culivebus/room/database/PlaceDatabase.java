package com.example.junyoung.culivebus.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.junyoung.culivebus.room.dao.PlaceDao;
import com.example.junyoung.culivebus.room.entity.UserPlace;

@Database(entities = {UserPlace.class}, version = 1)
public abstract class PlaceDatabase extends RoomDatabase {
  private static volatile PlaceDatabase INSTANCE;

  public abstract PlaceDao getPlaceDao();

  public static PlaceDatabase getPlaceDatabase(final Context context) {
    if (INSTANCE == null) {
      synchronized (PlaceDatabase.class) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
            PlaceDatabase.class, "place_database")
            .build();
        }
      }
    }

    return INSTANCE;
  }
}
