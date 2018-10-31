package com.example.junyoung.uiucbus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.junyoung.uiucbus.httpclient.pojos.Stop;
import com.example.junyoung.uiucbus.httpclient.repository.BusStopRepository;

import java.util.List;

public class BusStopViewModel extends ViewModel {
  private BusStopRepository mBusStopRepo;
  private final MutableLiveData<List<Stop>> mBusStopList = new MutableLiveData<>();

  public BusStopViewModel() {
    mBusStopRepo = new BusStopRepository();
  }

  public void init(String latitude, String longitude) {
    mBusStopRepo.getBusStopList(mBusStopList, latitude, longitude);
  }

  public void loadNearestBusStopList(String latitude, String longitude) {
    mBusStopRepo.getBusStopList(mBusStopList, latitude, longitude);
  }

  public LiveData<List<Stop>> getBusStopList() {
    return mBusStopList;
  }
}