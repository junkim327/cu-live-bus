package com.example.junyoung.culivebus.ui.common;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedUserLocationViewModel extends ViewModel {
  private final MutableLiveData<LatLng> userLatLng = new MutableLiveData<>();

  @Inject
  public SharedUserLocationViewModel() {}

  public void select(LatLng latLng) {
    userLatLng.setValue(latLng);
  }

  public LiveData<LatLng> getUserLatLng() {
    return userLatLng;
  }
}
