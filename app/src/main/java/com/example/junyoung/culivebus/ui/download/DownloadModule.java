package com.example.junyoung.culivebus.ui.download;

import com.example.junyoung.culivebus.di.scope.FragmentScoped;
import com.example.junyoung.culivebus.di.ViewModelKey;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class DownloadModule {
  @FragmentScoped
  @ContributesAndroidInjector
  abstract DownloadFragment contributeDownloadFragment();

  @Binds
  @IntoMap
  @ViewModelKey(DownloadViewModel.class)
  abstract ViewModel bindDownloadViewModel(DownloadViewModel downloadViewModel);
}
