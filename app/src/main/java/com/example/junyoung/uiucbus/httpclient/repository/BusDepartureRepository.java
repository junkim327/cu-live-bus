package com.example.junyoung.uiucbus.httpclient.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.junyoung.uiucbus.Constants;
import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.pojos.Departure;
import com.example.junyoung.uiucbus.httpclient.pojos.DeparturesByStop;
import com.example.junyoung.uiucbus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.uiucbus.httpclient.services.DepartureService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

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

  private List<SortedDeparture> sortDeparturesByBus(List<Departure> departureList) {
    List<SortedDeparture> sortedDepartureList = new ArrayList<>();
    if (departureList != null && departureList.size() > 0) {
      List<String> busList = new ArrayList<>();
      for (Departure departure : departureList) {
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
