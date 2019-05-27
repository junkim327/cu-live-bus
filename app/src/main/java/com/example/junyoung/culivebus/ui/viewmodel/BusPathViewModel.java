package com.example.junyoung.culivebus.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.junyoung.culivebus.vo.Leg;
import com.example.junyoung.culivebus.httpclient.pojos.Path;
import com.example.junyoung.culivebus.db.entity.Shape;
import com.example.junyoung.culivebus.httpclient.repository.BusPathRepository;
import com.example.junyoung.culivebus.vo.Resource;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BusPathViewModel extends ViewModel {
  private final MutableLiveData<Resource<List<Shape>>> mShapeResponse = new MutableLiveData<>();
  private BusPathRepository mBusPathRepo;
  private final MutableLiveData<Resource<List<Path>>> mResponse = new MutableLiveData<>();
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  public BusPathViewModel() {
    mBusPathRepo = new BusPathRepository();
  }

  public void initShapeList(String shapeId) {
    mDisposable.add(mBusPathRepo.getBusPath(shapeId)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(shapes -> mShapeResponse.setValue(Resource.success(shapes)),
        throwable -> mShapeResponse.setValue(Resource.success(null))
      )
    );
  }

  public void initPathList(List<Leg> busList) {
    mDisposable.add(mBusPathRepo.getBusPathList(busList)
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(pathList -> mResponse.setValue(Resource.success(pathList)),
        throwable -> mResponse.setValue(Resource.success(null))
    ));
  }

  public LiveData<Resource<List<Shape>>> getShapeResponse() {
    return mShapeResponse;
  }

  public LiveData<Resource<List<Path>>> getBusPathList() {
    return mResponse;
  }

  @Override
  protected void onCleared() {
    mDisposable.clear();
  }
}
