package com.example.junyoung.culivebus.ui.permission;


import com.example.junyoung.culivebus.di.scope.FragmentScoped;
import com.example.junyoung.culivebus.di.ViewModelKey;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class PermissionModule {
  @FragmentScoped
  @ContributesAndroidInjector
  abstract PermissionFragment contributePermissionFragment();

  @Binds
  @IntoMap
  @ViewModelKey(PermissionViewModel.class)
  abstract ViewModel bindPermissionViewModel(PermissionViewModel permissionViewModel);
}
