package com.example.junyoung.culivebus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.junyoung.culivebus.httpclient.pojos.Stop;
import com.example.junyoung.culivebus.httpclient.repository.BusStopRepository;
import com.example.junyoung.culivebus.vo.Response;

import java.util.List;

public class BusStopViewModel extends ViewModel {
  private final BusStopRepository mBusStopRepo;
  private final MutableLiveData<Response<List<Stop>>> mBusStopResponse = new MutableLiveData<>();

  public BusStopViewModel() {
    mBusStopRepo = new BusStopRepository();
  }

  public void loadNearBusStopList(String latitude, String longitude) {
    mBusStopRepo.getBusStopList(mBusStopResponse, latitude, longitude);
  }

  public void requestSearchResult(String newText) {
    mBusStopRepo.getBusStopSearchResult(mBusStopResponse, newText);
  }

  public LiveData<Response<List<Stop>>> getBusStopList() {
    return mBusStopResponse;
  }
}