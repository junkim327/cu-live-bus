package com.example.junyoung.uiucbus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.junyoung.uiucbus.httpclient.pojos.Itinerary;

public class SharedTripViewModel extends ViewModel {
  final MutableLiveData<Itinerary> mItinerary = new MutableLiveData<>();

  public void select(Itinerary itinerary) {
    mItinerary.setValue(itinerary);
  }

  public LiveData<Itinerary> getItinerary() {
    return mItinerary;
  }
}
