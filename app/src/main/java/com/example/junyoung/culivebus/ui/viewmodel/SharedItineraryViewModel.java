package com.example.junyoung.culivebus.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.junyoung.culivebus.vo.Itinerary;

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
