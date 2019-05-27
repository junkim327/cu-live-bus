package com.example.junyoung.culivebus.di;

import com.example.junyoung.culivebus.viewmodel.CumtdViewModelFactory;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

@Module
abstract class ViewModelModule {
  @Binds
  abstract ViewModelProvider.Factory bindViewModelFactory(CumtdViewModelFactory factory);
}
