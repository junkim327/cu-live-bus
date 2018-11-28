package com.example.junyoung.culivebus.ui.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.junyoung.culivebus.RouteInfoDataSource;
import com.example.junyoung.culivebus.ui.viewmodel.DirectionInfoViewModel;

public class DirectionViewModelFactory implements ViewModelProvider.Factory {
  private final RouteInfoDataSource mDataSource;

  public DirectionViewModelFactory(RouteInfoDataSource dataSource) {
    mDataSource = dataSource;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(DirectionInfoViewModel.class)) {
      return (T) new DirectionInfoViewModel(mDataSource);
    }
    throw new IllegalArgumentException("Unknown ViewModel class");
  }
}
