package com.example.junyoung.uiucbus.ui.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.junyoung.uiucbus.UserSearchedBusStopDataSource;
import com.example.junyoung.uiucbus.ui.viewmodel.UserSearchedBusStopViewModel;

public class UserSearchedBusStopViewModelFactory implements ViewModelProvider.Factory {
  private final UserSearchedBusStopDataSource mDataSource;

  public UserSearchedBusStopViewModelFactory(UserSearchedBusStopDataSource dataSource) {
    mDataSource = dataSource;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(UserSearchedBusStopViewModel.class)) {
      return (T) new UserSearchedBusStopViewModel(mDataSource);
    }
    throw new IllegalArgumentException("Unknown ViewModel class");
  }
}
