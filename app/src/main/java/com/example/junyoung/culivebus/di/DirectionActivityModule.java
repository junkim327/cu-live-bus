package com.example.junyoung.culivebus.di;

import com.example.junyoung.culivebus.DirectionActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class DirectionActivityModule {
  @ContributesAndroidInjector(modules = DirectionFragmentBuildersModule.class)
  abstract DirectionActivity contributeDirectionActivity();
}
