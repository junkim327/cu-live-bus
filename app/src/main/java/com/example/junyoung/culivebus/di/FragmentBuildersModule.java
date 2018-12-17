package com.example.junyoung.culivebus.di;

import com.example.junyoung.culivebus.fragment.DepartureFragment;
import com.example.junyoung.culivebus.fragment.DashboardFragment;
import com.example.junyoung.culivebus.ui.download.DownloadFragment;
import com.example.junyoung.culivebus.ui.search.SearchFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
  @ContributesAndroidInjector
  abstract DownloadFragment contributeDownloadFragment();

  @ContributesAndroidInjector
  abstract DashboardFragment contributeDashboardFragment();

  @ContributesAndroidInjector
  abstract SearchFragment contributeBusStopSearchFragment();

  @ContributesAndroidInjector
  abstract DepartureFragment contributeBusDeparturesFragment();
}
