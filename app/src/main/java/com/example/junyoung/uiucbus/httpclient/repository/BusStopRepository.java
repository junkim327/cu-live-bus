package com.example.junyoung.uiucbus.httpclient.repository;

import android.arch.lifecycle.MutableLiveData;

import com.example.junyoung.uiucbus.Constants;
import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.pojos.BusStops;
import com.example.junyoung.uiucbus.httpclient.pojos.Stop;
import com.example.junyoung.uiucbus.httpclient.services.BusStopService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusStopRepository {
  private static final String NUM_STOPS = "10";
  private BusStopService mBusStopService;

  public BusStopRepository() {
    mBusStopService = RetrofitBuilder.getRetrofitandRxJavaInstance()
      .create(BusStopService.class);
  }

  public void getBusStopList(MutableLiveData<List<Stop>> data, String latitude, String longitude) {
    mBusStopService.getNearestStops(Constants.API_KEY, latitude, longitude, NUM_STOPS)
      .enqueue(new Callback<BusStops>() {
        @Override
        public void onResponse(Call<BusStops> call, Response<BusStops> response) {
          if (response.isSuccessful()) {
            if (response.body() != null) {
              data.setValue(response.body().getStops());
            }
          }
        }

        @Override
        public void onFailure(Call<BusStops> call, Throwable t) {

        }
      });
  }
}
