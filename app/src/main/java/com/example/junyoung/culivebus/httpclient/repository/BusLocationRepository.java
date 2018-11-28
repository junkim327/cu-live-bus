package com.example.junyoung.culivebus.httpclient.repository;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.httpclient.RetrofitBuilder;
import com.example.junyoung.culivebus.httpclient.pojos.Vehicle;
import com.example.junyoung.culivebus.httpclient.pojos.VehicleData;
import com.example.junyoung.culivebus.httpclient.services.BusLocationService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusLocationRepository {
  private BusLocationService mBusLocationService;

  public BusLocationRepository() {
    mBusLocationService = RetrofitBuilder.getRetrofitandRxJavaInstance()
      .create(BusLocationService.class);
  }

  public void getBusLocation(MutableLiveData<Vehicle> data, String busId) {
    mBusLocationService.getBusLocation(Constants.API_KEY, busId)
      .enqueue(new Callback<VehicleData>() {
        @Override
        public void onResponse(@NonNull Call<VehicleData> call, @NonNull Response<VehicleData> response) {
          if (response.isSuccessful()) {
            if (response.body() != null) {
              data.setValue(response.body().getVehicles().get(0));
            }
          }
        }

        @Override
        public void onFailure(@NonNull Call<VehicleData> call, @NonNull Throwable t) {

        }
      });
  }
}
