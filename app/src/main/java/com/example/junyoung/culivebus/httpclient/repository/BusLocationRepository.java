package com.example.junyoung.culivebus.httpclient.repository;

import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.httpclient.RetrofitBuilder;
import com.example.junyoung.culivebus.httpclient.pojos.Vehicle;
import com.example.junyoung.culivebus.vo.response.VehicleResponse;
import com.example.junyoung.culivebus.httpclient.services.BusLocationService;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusLocationRepository {
  private BusLocationService mBusLocationService;

  public BusLocationRepository() {
    mBusLocationService = RetrofitBuilder.getRetrofitandRxJavaInstance()
      .create(BusLocationService.class);
  }

  public Single<Vehicle> getBus(String busId) {
    return mBusLocationService.getVehicleSingle(Constants.API_KEY, busId)
      .map(vehicleResponse -> vehicleResponse.getVehicles().get(0));
  }

  public void getBusLocation(MutableLiveData<Vehicle> data, String busId) {
    mBusLocationService.getBusLocation(Constants.API_KEY, busId)
      .enqueue(new Callback<VehicleResponse>() {
        @Override
        public void onResponse(@NonNull Call<VehicleResponse> call, @NonNull Response<VehicleResponse> response) {
          if (response.isSuccessful()) {
            if (response.body() != null) {
              data.setValue(response.body().getVehicles().get(0));
            }
          }
        }

        @Override
        public void onFailure(@NonNull Call<VehicleResponse> call, @NonNull Throwable t) {

        }
      });
  }
}
