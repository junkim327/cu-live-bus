package com.example.junyoung.culivebus.di;

import com.example.junyoung.culivebus.fragment.BusDeparturesFragment;
import com.example.junyoung.culivebus.fragment.DashboardFragment;
import com.example.junyoung.culivebus.ui.stopsearch.BusStopSearchFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
  @ContributesAndroidInjector
  abstract DashboardFragment contributeDashboardFragment();

  @ContributesAndroidInjector
  abstract BusStopSearchFragment contributeBusStopSearchFragment();

  @ContributesAndroidInjector
  abstract BusDeparturesFragment contributeBusDeparturesFragment();
}
