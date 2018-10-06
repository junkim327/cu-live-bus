package com.example.junyoung.uiucbus.ui.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.junyoung.uiucbus.PlaceDataSource;
import com.example.junyoung.uiucbus.ui.viewmodel.PlaceViewModel;

public class PlaceViewModelFactory implements ViewModelProvider.Factory {
  private final PlaceDataSource mPlaceDataSource;

  public PlaceViewModelFactory(PlaceDataSource dataSource) {
    mPlaceDataSource = dataSource;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(PlaceViewModel.class)) {
      return (T) new PlaceViewModel(mPlaceDataSource);
    }
    throw new IllegalArgumentException("Unknown ViewModel class");
  }
}
