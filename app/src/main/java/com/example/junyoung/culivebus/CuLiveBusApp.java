package com.example.junyoung.culivebus;

import android.app.Activity;
import android.app.Application;

import com.example.junyoung.culivebus.di.AppInjector;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class CuLiveBusApp extends Application implements HasActivityInjector {
  @Inject
  DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

  @Override
  public void onCreate() {
    super.onCreate();
    AppInjector.init(this);
  }

  @Override
  public AndroidInjector<Activity> activityInjector() {
    return dispatchingAndroidInjector;
  }
}
