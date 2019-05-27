package com.example.junyoung.culivebus.di;

import com.example.junyoung.culivebus.ui.direction.result.DirectionResultFragment;
import com.example.junyoung.culivebus.ui.direction.search.DirectionSearchFragment;
import com.example.junyoung.culivebus.ui.direction.search.SearchHistoryFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class DirectionFragmentBuildersModule {
  @ContributesAndroidInjector
  abstract DirectionResultFragment contributeDirectionResultFragment();

  @ContributesAndroidInjector
  abstract DirectionSearchFragment contributeDirectionSearchFragment();

  @ContributesAndroidInjector
  abstract SearchHistoryFragment contributeSearchHistoryFragment();
}
