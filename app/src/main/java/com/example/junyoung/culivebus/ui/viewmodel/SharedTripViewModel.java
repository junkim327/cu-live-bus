package com.example.junyoung.culivebus.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.junyoung.culivebus.vo.Itinerary;

public class SharedTripViewModel extends ViewModel {
  final MutableLiveData<Itinerary> mItinerary = new MutableLiveData<>();

  public void select(Itinerary itinerary) {
    mItinerary.setValue(itinerary);
  }

  public LiveData<Itinerary> getItinerary() {
    return mItinerary;
  }
}
