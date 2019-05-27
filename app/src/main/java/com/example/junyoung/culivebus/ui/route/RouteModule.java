package com.example.junyoung.culivebus.ui.route;

import com.example.junyoung.culivebus.di.ViewModelKey;
import com.example.junyoung.culivebus.di.scope.FragmentScoped;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class RouteModule {
  @Binds
  abstract AppCompatActivity provideAppCompatActivity(RouteActivity routeActivity);

  @FragmentScoped
  @ContributesAndroidInjector
  abstract RouteFragment contributeRouteFragment();

  @Binds
  @IntoMap
  @ViewModelKey(RouteViewModel.class)
  abstract ViewModel bindRouteViewModel(RouteViewModel routeViewModel);
}
