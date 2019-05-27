package com.example.junyoung.culivebus.repository;

import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.api.CumtdService;
import com.example.junyoung.culivebus.vo.StopTime;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.vo.response.StopTimesResponse;

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
public class StopTimesRepository {
  private final CumtdService cumtdService;

  @Inject
  public StopTimesRepository(CumtdService cumtdService) {
    this.cumtdService = cumtdService;
  }

  public LiveData<Resource<List<StopTime>>> getStopTimesByTrip(String tripId) {
    MutableLiveData<Resource<List<StopTime>>> resource = new MutableLiveData<>();
    resource.setValue(Resource.loading(null));

    cumtdService.getStopTimesByTrip(Constants.API_KEY, tripId)
      .enqueue(new Callback<StopTimesResponse>() {
      @Override
      public void onResponse(Call<StopTimesResponse> call, Response<StopTimesResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
          resource.setValue(Resource.success(response.body().getStopTimes()));
        } else {
          String message = null;
          if (response.errorBody() != null) {
            try {
              message = response.errorBody().string();
            } catch (IOException ignored) {
              Timber.e(ignored, "error while parsing");
            }
          }

          if (message == null || message.trim().length() == 0) {
            message = response.message();
          }
          resource.setValue(Resource.error(message, null));
        }
      }

      @Override
      public void onFailure(Call<StopTimesResponse> call, Throwable t) {
        resource.setValue(Resource.error(t.getMessage(), null));
      }
    });

    return resource;
  }
}
