package com.example.junyoung.culivebus.ui.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.junyoung.culivebus.httpclient.pojos.Departure;
import com.example.junyoung.culivebus.vo.SortedDeparture;

import javax.inject.Inject;

public class SharedDepartureViewModel extends ViewModel {
  private final MutableLiveData<Departure> departure = new MutableLiveData<>();

  @Inject
  public SharedDepartureViewModel() {

  }

  public void select(Departure departure) {
    this.departure.setValue(departure);
  }

  public LiveData<Departure> getDeparture() {
    return departure;
  }
}
