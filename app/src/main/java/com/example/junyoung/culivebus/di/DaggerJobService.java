package com.example.junyoung.culivebus.di;

import android.app.job.JobService;

import dagger.android.AndroidInjection;

public abstract class DaggerJobService extends JobService {
  @Override
  public void onCreate() {
    super.onCreate();
    AndroidInjection.inject(this);
  }
}
