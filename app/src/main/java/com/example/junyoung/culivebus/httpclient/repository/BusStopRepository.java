package com.example.junyoung.culivebus.httpclient.repository;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.httpclient.RetrofitBuilder;
import com.example.junyoung.culivebus.httpclient.pojos.BusStops;
import com.example.junyoung.culivebus.httpclient.pojos.Stop;
import com.example.junyoung.culivebus.httpclient.services.BusStopService;
import com.example.junyoung.culivebus.vo.Response;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class BusStopRepository {
  private static final String NUM_STOPS = "10";
  private BusStopService mBusStopService;

  public BusStopRepository() {
    mBusStopService = RetrofitBuilder.getRetrofitandRxJavaInstance()
      .create(BusStopService.class);
  }

  public void getBusStopList(MutableLiveData<Response<List<Stop>>> busStopResponse,
                             String latitude, String longitude) {
    mBusStopService.getNearestStops(Constants.API_KEY, latitude, longitude, NUM_STOPS)
      .enqueue(new Callback<BusStops>() {
        @Override
        public void onResponse(Call<BusStops> call, retrofit2.Response<BusStops> response) {
          if (response.isSuccessful()) {
            if (response.body() != null) {
              busStopResponse.setValue(Response.success(response.body().getStops()));
            }
          } else {
            // busStopResponse.setValue(Response.error())
          }
        }

        @Override
        public void onFailure(Call<BusStops> call, Throwable t) {
          busStopResponse.setValue(Response.error(t));
        }
      });
  }

  public void getBusStopSearchResult(MutableLiveData<Response<List<Stop>>> busStopResponse,
                                     String query) {
    mBusStopService.getStopsBySearch(Constants.API_KEY, query)
      .enqueue(new Callback<BusStops>() {
        @Override
        public void onResponse(Call<BusStops> call, retrofit2.Response<BusStops> response) {
          if (response.isSuccessful()) {
            if (response.body() != null) {
              busStopResponse.setValue(Response.success(response.body().getStops()));
            }
          } else {

          }
        }

        @Override
        public void onFailure(@NonNull Call<BusStops> call, @NonNull Throwable t) {
          busStopResponse.setValue(Response.error(t));
        }
      });
  }
}
