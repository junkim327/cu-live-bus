package com.example.junyoung.uiucbus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.junyoung.uiucbus.httpclient.pojos.Itinerary;

import java.util.List;

public class SharedItineraryViewModel extends ViewModel {
  private final MutableLiveData<List<Itinerary>> mItineraryList = new MutableLiveData<>();

  public void select(List<Itinerary> itineraryList) {
    mItineraryList.setValue(itineraryList);
  }

  public LiveData<List<Itinerary>> getItineraryList() {
    return mItineraryList;
  }
}
