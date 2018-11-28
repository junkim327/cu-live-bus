package com.example.junyoung.culivebus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.junyoung.culivebus.httpclient.pojos.Leg;
import com.example.junyoung.culivebus.httpclient.pojos.Path;
import com.example.junyoung.culivebus.httpclient.repository.BusPathRepository;
import com.example.junyoung.culivebus.vo.Response;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class BusPathViewModel extends ViewModel {
  private MutableLiveData<Path> mPath = new MutableLiveData<>();
  private BusPathRepository mBusPathRepo;
  private final MutableLiveData<Response<List<Path>>> mResponse = new MutableLiveData<>();
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  public BusPathViewModel() {
    mBusPathRepo = new BusPathRepository();
  }

  public void init(String shapeId) {
    mPath = mBusPathRepo.getBusPath(mPath, shapeId);
  }

  public void requestPathList(List<Leg> busList) {
    mDisposable.add(mBusPathRepo.getBusPathList(busList)
      .observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe(__ -> mResponse.setValue(Response.loading()))
      .subscribe(pathList -> mResponse.setValue(Response.success(pathList)),
        throwable -> mResponse.setValue(Response.error(throwable)))
    );
  }

  public LiveData<Path> getBusPath() {
    return mPath;
  }

  public LiveData<Response<List<Path>>> getBusPathList() {
    return mResponse;
  }

  @Override
  protected void onCleared() {
    mDisposable.dispose();
  }
}
