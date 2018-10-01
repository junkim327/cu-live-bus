package com.example.junyoung.uiucbus.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.junyoung.uiucbus.RouteInfoDataSource;
import com.example.junyoung.uiucbus.ui.viewmodel.RouteInfoViewModel;

public class DirectionViewModelFactory implements ViewModelProvider.Factory {
  private final RouteInfoDataSource mDataSource;

  public DirectionViewModelFactory(RouteInfoDataSource dataSource) {
    mDataSource = dataSource;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(RouteInfoViewModel.class)) {
      return (T) new RouteInfoViewModel(mDataSource);
    }
    throw new IllegalArgumentException("Unknown ViewModel class");
  }
}
