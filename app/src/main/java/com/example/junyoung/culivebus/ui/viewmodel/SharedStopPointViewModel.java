package com.example.junyoung.culivebus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.junyoung.culivebus.room.entity.StopPoint;

public class SharedStopPointViewModel extends ViewModel {
  private final MutableLiveData<StopPoint> mSelectedStopPoint = new MutableLiveData<>();

  public void select(StopPoint stopPoint) {
    mSelectedStopPoint.setValue(stopPoint);
  }

  public LiveData<StopPoint> getSelectedStopPoint() {
    return mSelectedStopPoint;
  }
}
