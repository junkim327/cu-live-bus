package com.example.junyoung.culivebus.httpclient.repository;

import android.arch.lifecycle.MutableLiveData;

import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.httpclient.RetrofitBuilder;
import com.example.junyoung.culivebus.httpclient.pojos.StopTimes;
import com.example.junyoung.culivebus.httpclient.pojos.StopTimesByTrip;
import com.example.junyoung.culivebus.httpclient.services.StopTimesService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusRouteRepository {
  private StopTimesService mStopTimesService;

  public BusRouteRepository() {
    mStopTimesService = RetrofitBuilder.getRetrofitandRxJavaInstance()
      .create(StopTimesService.class);
  }

  public MutableLiveData<List<StopTimes>> getRouteList(String tripId) {
    final MutableLiveData<List<StopTimes>> data = new MutableLiveData<>();
    mStopTimesService.getStopTimesByTrip(Constants.API_KEY, tripId)
      .enqueue(new Callback<StopTimesByTrip>() {
        @Override
        public void onResponse(Call<StopTimesByTrip> call, Response<StopTimesByTrip> response) {
          if (response.isSuccessful()) {
            if (response.body() != null) {
              data.setValue(response.body().getStopTimes());
            }
          }
        }

        @Override
        public void onFailure(Call<StopTimesByTrip> call, Throwable t) {

        }
      });

    return data;
  }
}
