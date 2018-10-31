package com.example.junyoung.uiucbus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.junyoung.uiucbus.httpclient.pojos.StopTimes;
import com.example.junyoung.uiucbus.httpclient.pojos.Vehicle;
import com.example.junyoung.uiucbus.httpclient.repository.BusLocationRepository;
import com.example.junyoung.uiucbus.httpclient.repository.BusRouteRepository;

import java.util.List;

public class BusRouteViewModel extends ViewModel {
  private BusRouteRepository mBusRouteRepo;
  private BusLocationRepository mBusLocationRepo;
  private MutableLiveData<List<StopTimes>> mRouteList;
  private final MutableLiveData<Vehicle> mBusLocation = new MutableLiveData<>();

  public BusRouteViewModel() {
    mBusRouteRepo = new BusRouteRepository();
    mBusLocationRepo = new BusLocationRepository();
  }

  public void initRouteList(String tripId) {
    if (mRouteList != null) {
      return;
    }
    mRouteList = mBusRouteRepo.getRouteList(tripId);
  }

  public void initBusLocation(String busId) {
    mBusLocationRepo.getBusLocation(mBusLocation, busId);
  }

  public LiveData<List<StopTimes>> getRouteList() {
    return mRouteList;
  }

  public LiveData<Vehicle> getBusLocation() {
    return mBusLocation;
  }
}
