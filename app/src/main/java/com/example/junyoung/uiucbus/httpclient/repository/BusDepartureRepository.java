package com.example.junyoung.uiucbus.httpclient.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.junyoung.uiucbus.Constants;
import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.pojos.Departure;
import com.example.junyoung.uiucbus.httpclient.pojos.DeparturesByStop;
import com.example.junyoung.uiucbus.httpclient.pojos.FavoriteBusDepartures;
import com.example.junyoung.uiucbus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.uiucbus.httpclient.services.DepartureService;
import com.example.junyoung.uiucbus.room.entity.UserSavedBusStop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class BusDepartureRepository {
  private DepartureService mDepartureService;

  public BusDepartureRepository() {
    mDepartureService = RetrofitBuilder.getRetrofitandRxJavaInstance()
      .create(DepartureService.class);
  }

  public Observable<List<FavoriteBusDepartures>> getUserFavoriteDepartures(
    List<UserSavedBusStop> userSavedBusStopList) {
    return Observable.interval(0, 10, TimeUnit.SECONDS).timeInterval()
      .flatMap((Function<Timed<Long>, ObservableSource<List<FavoriteBusDepartures>>>) longTimed
        -> createZippedObservable(userSavedBusStopList));
  }

  private Observable<List<FavoriteBusDepartures>> createZippedObservable(
    List<UserSavedBusStop> userSavedBusStopList) {
    final List<Observable<DeparturesByStop>> observableList = new ArrayList<>();
    for (int i = 0; i < userSavedBusStopList.size(); i++) {
      observableList.add(mDepartureService.getDeparturesByStop(Constants.API_KEY,
        userSavedBusStopList.get(i).getSavedStopId())
        .subscribeOn(Schedulers.io()));
    }

    return Observable.zip(observableList, objects -> {
      final List<FavoriteBusDepartures> departureList = new ArrayList<>();
      if (objects != null) {
        for (int i = 0; i < objects.length; i++) {
          departureList.add(new FavoriteBusDepartures(false, null, userSavedBusStopList.get(i)));
          departureList.add(new FavoriteBusDepartures(true, (DeparturesByStop) objects[i], null));
        }
      }
      return departureList;
    });
  }

  public MutableLiveData<List<SortedDeparture>> getDepartureList(String busStopId) {
    final MutableLiveData<List<SortedDeparture>> data = new MutableLiveData<>();
    mDepartureService.getDeparturesByStop1(Constants.API_KEY, busStopId)
      .enqueue(new Callback<DeparturesByStop>() {
      @Override
      public void onResponse(Call<DeparturesByStop> call, Response<DeparturesByStop> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            Log.d("BusRepo", "Size: " + response.body().getDepartures().size());

            data.setValue(sortDeparturesByBus(response.body().getDepartures()));
          }
        }
      }

      @Override
      public void onFailure(Call<DeparturesByStop> call, Throwable t) {

      }
    });

    return data;
  }

  public MutableLiveData<List<SortedDeparture>> updateDepartureList(String busStopId,
    MutableLiveData<List<SortedDeparture>> data) {
    mDepartureService.getDeparturesByStop1(Constants.API_KEY, busStopId)
      .enqueue(new Callback<DeparturesByStop>() {
        @Override
        public void onResponse(Call<DeparturesByStop> call, Response<DeparturesByStop> response) {
          if (response.isSuccessful()) {
            if (response.body() != null) {
              Log.d("BusRepo", "Size: " + response.body().getDepartures().size());

              data.setValue(sortDeparturesByBus(response.body().getDepartures()));
            }
          }
        }

        @Override
        public void onFailure(Call<DeparturesByStop> call, Throwable t) {

        }
      });

    return data;
  }

  private List<SortedDeparture> sortDeparturesByBus(List<Departure> departureList) {
    List<SortedDeparture> sortedDepartureList = new ArrayList<>();
    if (departureList != null && departureList.size() > 0) {
      List<String> busList = new ArrayList<>();
      for (Departure departure : departureList) {
        Log.d("BusDepartureRepository", departure.getExpected());
        int busIndex = busList.indexOf(departure.getHeadsign());
        if (busIndex == -1) {
          busList.add(departure.getHeadsign());
          sortedDepartureList.add(new SortedDeparture(departure));
        } else {
          sortedDepartureList.get(busIndex).storeDepartureInfo(departure);
        }
      }
    }

    return sortedDepartureList;
  }
}
