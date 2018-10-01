package com.example.junyoung.uiucbus.ui;

import android.content.Context;

import com.example.junyoung.uiucbus.RouteInfoDataSource;
import com.example.junyoung.uiucbus.room.database.RouteInfoDatabase;
import com.example.junyoung.uiucbus.room.datasource.LocalRouteInfoDataSource;

public class Injection {
  public static RouteInfoDataSource provideRouteInfoDataSource(Context context) {
    RouteInfoDatabase database = RouteInfoDatabase.getRouteInfoDatabase(context);
    return new LocalRouteInfoDataSource(database.getRouteInfoDao());
  }

  public static DirectionViewModelFactory provideDirectionViewModelFactory(Context context) {
    RouteInfoDataSource dataSource = provideRouteInfoDataSource(context);
    return new DirectionViewModelFactory(dataSource);
  }
}
