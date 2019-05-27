package com.example.junyoung.culivebus.ui.departure;

import com.example.junyoung.culivebus.di.ViewModelKey;
import com.example.junyoung.culivebus.di.scope.FragmentScoped;
import com.example.junyoung.culivebus.ui.common.SharedDepartureViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class DepartureModule {
  @Binds
  abstract AppCompatActivity provideAppCompatActivity(DepartureActivity departureActivity);

  @FragmentScoped
  @ContributesAndroidInjector
  abstract DepartureFragment contributeDepartureFragment();

  @Binds
  @IntoMap
  @ViewModelKey(DepartureViewModel.class)
  abstract ViewModel bindDepartureViewModel(DepartureViewModel departureViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(SharedDepartureViewModel.class)
  abstract ViewModel bindSharedDepartureViewModel(SharedDepartureViewModel sharedDepartureViewModel);
}
