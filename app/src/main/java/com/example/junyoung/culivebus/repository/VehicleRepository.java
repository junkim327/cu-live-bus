package com.example.junyoung.culivebus.repository;

import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.api.CumtdService;
import com.example.junyoung.culivebus.httpclient.pojos.Vehicle;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.vo.response.VehicleResponse;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class VehicleRepository {
  private final CumtdService cumtdService;

  @Inject
  public VehicleRepository(CumtdService cumtdService) {
    this.cumtdService = cumtdService;
  }

  public LiveData<Resource<List<Vehicle>>> getVehicle(String vehicleId) {
    MutableLiveData<Resource<List<Vehicle>>> resource = new MutableLiveData<>();
    resource.setValue(Resource.loading(null));

    cumtdService.getVehicle(Constants.API_KEY, vehicleId).enqueue(new Callback<VehicleResponse>() {
      @Override
      public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
          resource.setValue(Resource.success(response.body().getVehicles()));
        } else {
          String message = null;
          if (response.errorBody() != null) {
            try {
              message = response.errorBody().string();
            } catch (IOException ignored) {
              Timber.e(ignored, "error while parsing response");
            }
          }

          if (message == null || message.trim().length() == 0) {
            message = response.message();
          }
          resource.setValue(Resource.error(message, null));
        }
      }

      @Override
      public void onFailure(Call<VehicleResponse> call, Throwable t) {
        resource.setValue(Resource.error(t.getMessage(), null));
      }
    });

    return resource;
  }
}
