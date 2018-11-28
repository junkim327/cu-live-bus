package com.example.junyoung.culivebus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.junyoung.culivebus.httpclient.pojos.FavoriteBusDepartures;
import com.example.junyoung.culivebus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.culivebus.httpclient.repository.BusDepartureRepository;
import com.example.junyoung.culivebus.room.entity.UserSavedBusStop;
import com.example.junyoung.culivebus.vo.Response;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

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
      .subscribe(departuresByStops -> {mResponse.setValue(Response.success(departuresByStops));
      Log.d("ViewModel", "still loading...");},
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

  public void clear() {
    mDisposable.clear();
  }

  @Override
  protected void onCleared() {
    Log.d(BusDeparturesViewModel.class.getSimpleName(), "onCleared has called");
    mDisposable.dispose();
  }
}
