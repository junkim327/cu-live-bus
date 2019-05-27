package com.example.junyoung.culivebus.ui;

import com.example.junyoung.culivebus.di.ViewModelKey;
import com.example.junyoung.culivebus.ui.LaunchViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class LaunchModule {
  @Binds
  @IntoMap
  @ViewModelKey(LaunchViewModel.class)
  abstract ViewModel bindLaunchViewModel(LaunchViewModel launchViewModel);
}
