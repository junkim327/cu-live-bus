package com.example.junyoung.culivebus.di;

import com.example.junyoung.culivebus.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
  @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
  abstract MainActivity contributeMainActivity();
}
