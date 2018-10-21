package com.example.junyoung.uiucbus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.junyoung.uiucbus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.uiucbus.httpclient.repository.BusDepartureRepository;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class BusDeparturesViewModel extends ViewModel {
  private static final int ONE_MINUTE = 20000;

  private BusDepartureRepository mBusDepartureRepo;
  private LiveData<List<SortedDeparture>> mSortedDepartureList;

  public BusDeparturesViewModel() {
    mBusDepartureRepo = new BusDepartureRepository();
  }

  public void init(String busStopId) {
    mSortedDepartureList = mBusDepartureRepo.getDepartureList(busStopId);
  }

  public LiveData<List<SortedDeparture>> getSortedDepartureList() {
    return mSortedDepartureList;
  }
}
