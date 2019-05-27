package com.example.junyoung.culivebus.di;

import android.app.Application;
import android.content.Context;

import com.example.junyoung.culivebus.CuLiveBusApp;
import com.example.junyoung.culivebus.api.CumtdService;
import com.example.junyoung.culivebus.db.CuLiveBusDb;
import com.example.junyoung.culivebus.db.dao.BusStopDao;
import com.example.junyoung.culivebus.db.dao.ChangesetDao;
import com.example.junyoung.culivebus.db.dao.FavoriteStopDao;
import com.example.junyoung.culivebus.db.dao.RouteInfoDao;
import com.example.junyoung.culivebus.db.dao.SearchedPlaceDao;
import com.example.junyoung.culivebus.db.dao.ShapeDao;
import com.example.junyoung.culivebus.util.LiveDataCallAdapterFactory;
import com.example.junyoung.culivebus.util.pref.PreferenceStorage;
import com.example.junyoung.culivebus.util.pref.SharedPreferenceStorage;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
class AppModule {
  public static final String DATABASE_NAME = "cu-live-bus-db";

  @Provides
  Context provideContext(CuLiveBusApp app) {
    return app.getApplicationContext();
  }

  @Singleton @Provides
  PreferenceStorage providePreferenceStorage(Context context) {
    return new SharedPreferenceStorage(context);
  }

  @Singleton @Provides
  CumtdService provideCumtdService() {
    return new Retrofit.Builder()
      .baseUrl("https://developer.cumtd.com/api/v2.2/json/")
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addCallAdapterFactory(new LiveDataCallAdapterFactory())
      .build()
      .create(CumtdService.class);
  }

  @Singleton @Provides
  CuLiveBusDb provideDb(CuLiveBusApp app) {
    return Room.databaseBuilder(app, CuLiveBusDb.class, DATABASE_NAME).build();
  }

  @Singleton @Provides
  BusStopDao provideBusStopDao(CuLiveBusDb db) {
    return db.busStopDao();
  }

  @Singleton @Provides
  FavoriteStopDao provideFavoriteStopDao(CuLiveBusDb db) {
    return db.favoriteStopDao();
  }

  @Singleton @Provides
  ChangesetDao provideChangesetDao(CuLiveBusDb db) {
    return db.changesetDao();
  }

  @Singleton @Provides
  ShapeDao provideShapeDao(CuLiveBusDb db) {
    return db.shapeDao();
  }

  @Singleton @Provides
  SearchedPlaceDao provideSearhcedPlaceDao(CuLiveBusDb db) {
    return db.searchedPlaceDao();
  }

  @Singleton @Provides
  RouteInfoDao provideRouteInfoDao(CuLiveBusDb db) {
    return db.routeInfoDao();
  }
}
