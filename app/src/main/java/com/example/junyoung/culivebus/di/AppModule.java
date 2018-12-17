package com.example.junyoung.culivebus.di;

import android.app.Application;

import com.example.junyoung.culivebus.api.CumtdService;
import com.example.junyoung.culivebus.db.CuLiveBusDb;
import com.example.junyoung.culivebus.db.dao.BusStopDao;
import com.example.junyoung.culivebus.httpclient.RetrofitBuilder;
import com.example.junyoung.culivebus.util.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
  public static final String DATABASE_NAME = "cu-live-bus-db";

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
  CuLiveBusDb provideDb(Application app) {
    return Room.databaseBuilder(app, CuLiveBusDb.class, DATABASE_NAME).build();
  }

  @Singleton @Provides
  BusStopDao provideBusStopDao(CuLiveBusDb db) {
    return db.busStopDao();
  }
}
