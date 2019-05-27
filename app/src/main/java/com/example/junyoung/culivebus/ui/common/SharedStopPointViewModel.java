package com.example.junyoung.culivebus.ui.common;

import com.example.junyoung.culivebus.db.entity.StopPoint;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedStopPointViewModel extends ViewModel {
  private final MutableLiveData<StopPoint> stopPoint = new MutableLiveData<>();

  @Inject
  public SharedStopPointViewModel() {

  }

  public void select(StopPoint stopPoint) {
    this.stopPoint.setValue(stopPoint);
  }

  public LiveData<StopPoint> getStopPoint() {
    return stopPoint;
  }
}
