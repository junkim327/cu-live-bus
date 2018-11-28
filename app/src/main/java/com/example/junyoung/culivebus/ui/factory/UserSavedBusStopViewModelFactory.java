package com.example.junyoung.culivebus.ui.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.junyoung.culivebus.UserSavedBusStopDataSource;
import com.example.junyoung.culivebus.ui.viewmodel.SavedBusStopViewModel;

public class UserSavedBusStopViewModelFactory implements ViewModelProvider.Factory {
  private final UserSavedBusStopDataSource mDataSource;

  public UserSavedBusStopViewModelFactory(UserSavedBusStopDataSource dataSource) {
    mDataSource = dataSource;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(SavedBusStopViewModel.class)) {
      return (T) new SavedBusStopViewModel(mDataSource);
    }
    throw new IllegalArgumentException("Unknown ViewModel class");
  }
}
