package com.example.junyoung.culivebus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.junyoung.culivebus.httpclient.pojos.SortedDeparture;

public class SharedDepartureViewModel extends ViewModel {
  private final MutableLiveData<SortedDeparture> mDeparture = new MutableLiveData<>();

  public void select(SortedDeparture departure) {
    mDeparture.setValue(departure);
  }

  public LiveData<SortedDeparture> getDeparture() {
    return mDeparture;
  }
}
