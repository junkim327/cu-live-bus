package com.example.junyoung.culivebus.ui;

import com.example.junyoung.culivebus.MainActivity;
import com.example.junyoung.culivebus.di.scope.FragmentScoped;
import com.example.junyoung.culivebus.di.ViewModelKey;
import com.example.junyoung.culivebus.ui.common.SharedDepartureViewModel;
import com.example.junyoung.culivebus.ui.common.SharedStopPointViewModel;
import com.example.junyoung.culivebus.ui.common.SharedUserLocationViewModel;
import com.example.junyoung.culivebus.ui.dashboard.DashboardFragment;
import com.example.junyoung.culivebus.ui.dashboard.DashboardViewModel;
import com.example.junyoung.culivebus.ui.direction.SharedPlaceViewModel;
import com.example.junyoung.culivebus.ui.direction.result.DirectionResultFragment;
import com.example.junyoung.culivebus.ui.direction.search.DirectionSearchFragment;
import com.example.junyoung.culivebus.ui.direction.search.DirectionSearchViewModel;
import com.example.junyoung.culivebus.ui.nearbystop.NearByStopFragment;
import com.example.junyoung.culivebus.ui.nearbystop.NearByStopViewModel;
import com.example.junyoung.culivebus.ui.route.RouteViewModel;
import com.example.junyoung.culivebus.ui.search.SearchFragment;
import com.example.junyoung.culivebus.ui.search.SearchViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainModule {
  @Binds
  abstract AppCompatActivity provideAppCompatActivity(MainActivity mainActivity);

  @FragmentScoped
  @ContributesAndroidInjector
  abstract DashboardFragment contributeDashboardFragment();

  @FragmentScoped
  @ContributesAndroidInjector
  abstract SearchFragment contributeBusStopSearchFragment();

  @FragmentScoped
  @ContributesAndroidInjector
  abstract NearByStopFragment contributeNearByStopFragment();

  @FragmentScoped
  @ContributesAndroidInjector
  abstract DirectionResultFragment contributeDirectionResultFragment();

  @FragmentScoped
  @ContributesAndroidInjector
  abstract DirectionSearchFragment contributeDirectionSearchFragment();

  @Binds
  @IntoMap
  @ViewModelKey(DashboardViewModel.class)
  abstract ViewModel bindDashboardViewModel(DashboardViewModel dashboardViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(SearchViewModel.class)
  abstract ViewModel bindSearchViewModel(SearchViewModel searchViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(NearByStopViewModel.class)
  abstract ViewModel bindNearByStopViewModel(NearByStopViewModel nearByStopViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(DirectionSearchViewModel.class)
  abstract ViewModel bindDirectionSearchViewModel(DirectionSearchViewModel directionSearchViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(SharedPlaceViewModel.class)
  abstract ViewModel bindSharedPlaceViewModel(SharedPlaceViewModel sharedPlaceViewModel);
}
