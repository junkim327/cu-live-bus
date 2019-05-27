package com.example.junyoung.culivebus.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.example.junyoung.culivebus.httpclient.pojos.FavoriteBusDepartures;
import com.example.junyoung.culivebus.vo.SortedDeparture;
import com.example.junyoung.culivebus.httpclient.repository.BusDepartureRepository;
import com.example.junyoung.culivebus.room.entity.UserSavedBusStop;
import com.example.junyoung.culivebus.vo.Resource;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class BusDepartureViewModel extends ViewModel {
  private static final int FORTY_SECONDS = 40000;

  private Timer mTimer;
  private BusDepartureRepository busDepartureRepository;
  private MutableLiveData<List<SortedDeparture>> mSortedDepartureList = new MutableLiveData<>();
  private final CompositeDisposable mDisposable = new CompositeDisposable();
  private final MutableLiveData<Resource<List<FavoriteBusDepartures>>> mResponse = new MutableLiveData<>();


  public BusDepartureViewModel() {

  }

  public void init(String busStopId) {
    mTimer = new Timer();

    //mSortedDepartureList = busDepartureRepository.getDepartureList(busStopId);
    mTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        Log.d("BusDepartureViewModel", "get departure list");
      }
    }, 0, FORTY_SECONDS);
  }

  public void loadUserFavoriteDepartures(List<UserSavedBusStop> userSavedBusStopList) {
    mDisposable.add(busDepartureRepository.getUserFavoriteDepartures(userSavedBusStopList)
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(departuresByStops -> {mResponse.setValue(Resource.success(departuresByStops));
      Log.d("ViewModel", "still loading...");},
        throwable -> mResponse.setValue(Resource.success(null)))
    );
  }

  public LiveData<List<SortedDeparture>> getSortedDepartureList() {
    return mSortedDepartureList;
  }

  public MutableLiveData<Resource<List<FavoriteBusDepartures>>> getResponse() {
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
    Log.d(BusDepartureViewModel.class.getSimpleName(), "onCleared has called");
    mDisposable.dispose();
  }
}
