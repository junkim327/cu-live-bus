package com.example.junyoung.uiucbus.ui.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.junyoung.uiucbus.UserSavedBusStopDataSource;
import com.example.junyoung.uiucbus.ui.viewmodel.UserSavedBusStopViewModel;

public class UserSavedBusStopViewModelFactory implements ViewModelProvider.Factory {
  private final UserSavedBusStopDataSource mDataSource;

  public UserSavedBusStopViewModelFactory(UserSavedBusStopDataSource dataSource) {
    mDataSource = dataSource;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(UserSavedBusStopViewModel.class)) {
      return (T) new UserSavedBusStopViewModel(mDataSource);
    }
    throw new IllegalArgumentException("Unknown ViewModel class");
  }
}
