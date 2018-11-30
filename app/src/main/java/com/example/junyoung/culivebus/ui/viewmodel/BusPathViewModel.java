package com.example.junyoung.culivebus.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.junyoung.culivebus.httpclient.pojos.Leg;
import com.example.junyoung.culivebus.httpclient.pojos.Path;
import com.example.junyoung.culivebus.httpclient.pojos.Shape;
import com.example.junyoung.culivebus.httpclient.repository.BusPathRepository;
import com.example.junyoung.culivebus.vo.Response;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BusPathViewModel extends ViewModel {
  private final MutableLiveData<Response<List<Shape>>> mShapeResponse = new MutableLiveData<>();
  private BusPathRepository mBusPathRepo;
  private final MutableLiveData<Response<List<Path>>> mResponse = new MutableLiveData<>();
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  public BusPathViewModel() {
    mBusPathRepo = new BusPathRepository();
  }

  public void initShapeList(String shapeId) {
    mDisposable.add(mBusPathRepo.getBusPath(shapeId)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(shapes -> mShapeResponse.setValue(Response.success(shapes)),
        throwable -> mShapeResponse.setValue(Response.error(throwable))
      )
    );
  }

  public void initPathList(List<Leg> busList) {
    mDisposable.add(mBusPathRepo.getBusPathList(busList)
      .observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe(__ -> mResponse.setValue(Response.loading()))
      .subscribe(pathList -> mResponse.setValue(Response.success(pathList)),
        throwable -> mResponse.setValue(Response.error(throwable)))
    );
  }

  public LiveData<Response<List<Shape>>> getShapeResponse() {
    return mShapeResponse;
  }

  public LiveData<Response<List<Path>>> getBusPathList() {
    return mResponse;
  }

  @Override
  protected void onCleared() {
    mDisposable.clear();
  }
}
