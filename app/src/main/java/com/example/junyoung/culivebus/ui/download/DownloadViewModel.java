package com.example.junyoung.culivebus.ui.download;

import com.example.junyoung.culivebus.repository.BusStopRepository;
import com.example.junyoung.culivebus.vo.Resource;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class DownloadViewModel extends ViewModel {
  private final LiveData<Resource<Integer>> numStops;

  @Inject
  DownloadViewModel(BusStopRepository busStopRepository) {
    numStops = busStopRepository.insertBusStops();
  }

  public LiveData<Resource<Integer>> getNumStops() {
    return numStops;
  }
}
