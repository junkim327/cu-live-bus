package com.example.junyoung.uiucbus.ui;

import android.content.Context;

import com.example.junyoung.uiucbus.PlaceDataSource;
import com.example.junyoung.uiucbus.RouteInfoDataSource;
import com.example.junyoung.uiucbus.UserSavedBusStopDataSource;
import com.example.junyoung.uiucbus.UserSearchedBusStopDataSource;
import com.example.junyoung.uiucbus.room.database.PlaceDatabase;
import com.example.junyoung.uiucbus.room.database.RouteInfoDatabase;
import com.example.junyoung.uiucbus.room.database.UserSavedBusStopDatabase;
import com.example.junyoung.uiucbus.room.database.UserSearchedBusStopDatabase;
import com.example.junyoung.uiucbus.room.datasource.LocalPlaceDataSource;
import com.example.junyoung.uiucbus.room.datasource.LocalRouteInfoDataSource;
import com.example.junyoung.uiucbus.room.datasource.LocalUserSavedBusStopDataSource;
import com.example.junyoung.uiucbus.room.datasource.LocalUserSearchedBusStopDataSource;
import com.example.junyoung.uiucbus.ui.factory.DirectionViewModelFactory;
import com.example.junyoung.uiucbus.ui.factory.PlaceViewModelFactory;
import com.example.junyoung.uiucbus.ui.factory.UserSavedBusStopViewModelFactory;
import com.example.junyoung.uiucbus.ui.factory.UserSearchedBusStopViewModelFactory;

public class Injection {
  public static RouteInfoDataSource provideRouteInfoDataSource(Context context) {
    RouteInfoDatabase database = RouteInfoDatabase.getRouteInfoDatabase(context);
    return new LocalRouteInfoDataSource(database.getRouteInfoDao());
  }

  public static DirectionViewModelFactory provideDirectionViewModelFactory(Context context) {
    RouteInfoDataSource dataSource = provideRouteInfoDataSource(context);
    return new DirectionViewModelFactory(dataSource);
  }

  public static PlaceDataSource providePlaceDataSource(Context context) {
    PlaceDatabase database = PlaceDatabase.getPlaceDatabase(context);
    return new LocalPlaceDataSource(database.getPlaceDao());
  }

  public static PlaceViewModelFactory providePlaceViewModelFactory(Context context) {
    PlaceDataSource dataSource = providePlaceDataSource(context);
    return new PlaceViewModelFactory(dataSource);
  }

  public static UserSavedBusStopDataSource provideUserSavedBusStopDataSource(Context context) {
    UserSavedBusStopDatabase database =
      UserSavedBusStopDatabase.getUserSavedBusStopDatabase(context);
    return new LocalUserSavedBusStopDataSource(database.getUserSavedBusStopDao());
  }

  public static UserSavedBusStopViewModelFactory provideUserSavedBusStopViewModelFactory(Context context) {
    UserSavedBusStopDataSource dataSource = provideUserSavedBusStopDataSource(context);
    return new UserSavedBusStopViewModelFactory(dataSource);
  }

  public static UserSearchedBusStopDataSource provideUserSearchedBusStopDataSource(Context context) {
    UserSearchedBusStopDatabase database =
      UserSearchedBusStopDatabase.getUserSearchedBusStopDatabase(context);

    return new LocalUserSearchedBusStopDataSource(database.getUserSearchedBusStopDao());
  }

  public static UserSearchedBusStopViewModelFactory provideUserSearchedBusStopViewModelFactory(Context context) {
    UserSearchedBusStopDataSource dataSource = provideUserSearchedBusStopDataSource(context);

    return new UserSearchedBusStopViewModelFactory(dataSource);
  }
}
