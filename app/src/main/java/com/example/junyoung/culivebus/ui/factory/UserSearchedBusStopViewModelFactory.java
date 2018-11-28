package com.example.junyoung.culivebus.ui.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.junyoung.culivebus.UserSearchedBusStopDataSource;
import com.example.junyoung.culivebus.ui.viewmodel.SearchedBusStopViewModel;

public class UserSearchedBusStopViewModelFactory implements ViewModelProvider.Factory {
  private final UserSearchedBusStopDataSource mDataSource;

  public UserSearchedBusStopViewModelFactory(UserSearchedBusStopDataSource dataSource) {
    mDataSource = dataSource;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(SearchedBusStopViewModel.class)) {
      return (T) new SearchedBusStopViewModel(mDataSource);
    }
    throw new IllegalArgumentException("Unknown ViewModel class");
  }
}
