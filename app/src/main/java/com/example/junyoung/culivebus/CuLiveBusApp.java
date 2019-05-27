package com.example.junyoung.culivebus;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.example.junyoung.culivebus.di.DaggerAppComponent;
import com.jakewharton.threetenabp.AndroidThreeTen;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.DispatchingAndroidInjector;
import timber.log.Timber;

public class CuLiveBusApp extends DaggerApplication {
  @Inject
  DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

  @Override
  public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
    //LeakCanary.install(this);
    //AppInjector.init(this);
    AndroidThreeTen.init(this);
  }

  @Override
  protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
    return DaggerAppComponent.builder().create(this);
  }
}
