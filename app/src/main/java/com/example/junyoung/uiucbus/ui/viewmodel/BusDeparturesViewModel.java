package com.example.junyoung.uiucbus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.junyoung.uiucbus.httpclient.pojos.DeparturesByStop;
import com.example.junyoung.uiucbus.httpclient.pojos.FavoriteBusDepartures;
import com.example.junyoung.uiucbus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.uiucbus.httpclient.repository.BusDepartureRepository;
import com.example.junyoung.uiucbus.room.entity.UserSavedBusStop;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class BusDeparturesViewModel extends ViewModel {
  private static final int FORTY_SECONDS = 40000;

  private Timer mTimer;
  private BusDepartureRepository mBusDepartureRepo;
  private MutableLiveData<List<SortedDeparture>> mSortedDepartureList = new MutableLiveData<>();
  private final CompositeDisposable mDisposable = new CompositeDisposable();
  private final MutableLiveData<Response<List<FavoriteBusDepartures>>> mResponse = new MutableLiveData<>();

  public BusDeparturesViewModel() {
    mBusDepartureRepo = new BusDepartureRepository();
  }

  public void init(String busStopId) {
    mTimer = new Timer();

    //mSortedDepartureList = mBusDepartureRepo.getDepartureList(busStopId);
    mTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        Log.d("BusDeparturesViewModel", "get departure list");
        mSortedDepartureList = mBusDepartureRepo.updateDepartureList(busStopId, mSortedDepartureList);
      }
    }, 0, FORTY_SECONDS);
  }

  public void loadUserFavoriteDepartures(List<UserSavedBusStop> userSavedBusStopList) {
    mDisposable.add(mBusDepartureRepo.getUserFavoriteDepartures(userSavedBusStopList)
      .observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe(__ -> {mResponse.setValue(Response.loading());})
      .subscribe(departuresByStops -> mResponse.setValue(Response.success(departuresByStops)),
        throwable -> mResponse.setValue(Response.error(throwable)))
    );
  }

  public LiveData<List<SortedDeparture>> getSortedDepartureList() {
    return mSortedDepartureList;
  }

  public MutableLiveData<Response<List<FavoriteBusDepartures>>> getResponse() {
    return mResponse;
  }

  public void cancelTimer() {
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
  }

  @Override
  protected void onCleared() {
    mDisposable.dispose();
  }
}
