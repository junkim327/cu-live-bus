package com.example.junyoung.culivebus.di;

import com.example.junyoung.culivebus.di.scope.ActivityScoped;
import com.example.junyoung.culivebus.ui.LaunchModule;
import com.example.junyoung.culivebus.ui.LauncherActivity;
import com.example.junyoung.culivebus.MainActivity;
import com.example.junyoung.culivebus.ui.MainModule;
import com.example.junyoung.culivebus.ui.departure.DepartureActivity;
import com.example.junyoung.culivebus.ui.departure.DepartureModule;
import com.example.junyoung.culivebus.ui.download.DownloadActivity;
import com.example.junyoung.culivebus.ui.download.DownloadModule;
import com.example.junyoung.culivebus.ui.permission.PermissionActivity;
import com.example.junyoung.culivebus.ui.permission.PermissionModule;
import com.example.junyoung.culivebus.ui.route.RouteActivity;
import com.example.junyoung.culivebus.ui.route.RouteModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityBindingModule {
  @ActivityScoped
  @ContributesAndroidInjector(modules = LaunchModule.class)
  abstract LauncherActivity contributeLauncherActivity();

  @ActivityScoped
  @ContributesAndroidInjector(modules = MainModule.class)
  abstract MainActivity contributeMainActivity();

  @ActivityScoped
  @ContributesAndroidInjector(modules = PermissionModule.class)
  abstract PermissionActivity contributePermissionActivity();

  @ActivityScoped
  @ContributesAndroidInjector(modules = DownloadModule.class)
  abstract DownloadActivity contributeDownloadActivity();

  @ActivityScoped
  @ContributesAndroidInjector(modules = DepartureModule.class)
  abstract DepartureActivity contributeDepartureActivity();

  @ActivityScoped
  @ContributesAndroidInjector(modules = RouteModule.class)
  abstract RouteActivity contributeRouteActivity();
}
