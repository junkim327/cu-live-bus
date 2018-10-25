package com.example.junyoung.uiucbus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.junyoung.uiucbus.httpclient.pojos.Path;
import com.example.junyoung.uiucbus.httpclient.repository.BusPathRepository;

public class BusPathViewModel extends ViewModel {
  private LiveData<Path> mPath;
  private BusPathRepository mBusPathRepo;

  public BusPathViewModel() {
    mBusPathRepo = new BusPathRepository();
  }

  public void init(String shapeId) {
    mPath = mBusPathRepo.getBusPath(shapeId);
  }

  public LiveData<Path> getBusPath() {
    return mPath;
  }
}
