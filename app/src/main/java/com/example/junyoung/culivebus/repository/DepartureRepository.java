package com.example.junyoung.culivebus.repository;

import com.example.junyoung.culivebus.util.AppExecutors;
import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.api.CumtdService;
import com.example.junyoung.culivebus.httpclient.pojos.Departure;
import com.example.junyoung.culivebus.vo.SortedDeparture;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.vo.response.DeparturesResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class DepartureRepository {
  private final CumtdService cumtdService;
  private final AppExecutors appExecutors;

  @Inject
  public DepartureRepository(CumtdService cumtdService, AppExecutors appExecutors) {
    this.cumtdService = cumtdService;
    this.appExecutors = appExecutors;
  }

  public void getDeparturesByStop(String stopId,
                                  MutableLiveData<Resource<List<SortedDeparture>>> departures) {
    departures.postValue(Resource.loading(null));
    cumtdService.getDeparturesByStop(Constants.API_KEY, stopId).enqueue(new Callback<DeparturesResponse>() {
      @Override
      public void onResponse(Call<DeparturesResponse> call, Response<DeparturesResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
          Timber.d("Size %d", response.body().getDepartures().size());
          if (response.body().getDepartures().isEmpty()) {
            departures.setValue(Resource.error("empty", null));
          } else {
            departures.setValue(Resource.success(sortDepartures(response.body().getDepartures())));
          }
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
          departures.setValue(Resource.error(message, null));
        }
      }

      @Override
      public void onFailure(Call<DeparturesResponse> call, Throwable t) {
        departures.setValue(Resource.error(t.getMessage(), null));
      }
    });
  }

  private List<SortedDeparture> sortDepartures(List<Departure> departureList) {
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
