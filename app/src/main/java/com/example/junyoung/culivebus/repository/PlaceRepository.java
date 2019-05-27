package com.example.junyoung.culivebus.repository;

import com.example.junyoung.culivebus.util.AppExecutors;
import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.api.CumtdService;
import com.example.junyoung.culivebus.db.dao.RouteInfoDao;
import com.example.junyoung.culivebus.db.dao.SearchedPlaceDao;
import com.example.junyoung.culivebus.db.entity.RouteInfo;
import com.example.junyoung.culivebus.db.entity.SearchedPlace;
import com.example.junyoung.culivebus.vo.Itinerary;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.vo.response.PlannedTripsResponse;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class PlaceRepository {
  private final CumtdService cumtdService;
  private final SearchedPlaceDao searchedPlaceDao;
  private final RouteInfoDao routeInfoDao;
  private final AppExecutors appExecutors;

  @Inject
  public PlaceRepository(CumtdService cumtdService, SearchedPlaceDao searchedPlaceDao,
                         RouteInfoDao routeInfoDao, AppExecutors appExecutors) {
    this.cumtdService = cumtdService;
    this.searchedPlaceDao = searchedPlaceDao;
    this.routeInfoDao = routeInfoDao;
    this.appExecutors = appExecutors;
  }

  public LiveData<Resource<List<SearchedPlace>>> loadSevenSearchedPlaces() {
    return Transformations.map(searchedPlaceDao.loadSevenSearchedPlaces(), Resource::success);
  }

  public LiveData<Resource<List<SearchedPlace>>> loadAllSearchedPlaces() {
    return Transformations.map(searchedPlaceDao.loadAllSearchedPlaces(), Resource::success);
  }

  public void insertSearchedPlace(SearchedPlace searchedPlace) {
    appExecutors.diskIO().execute(() -> searchedPlaceDao.insertPlace(searchedPlace));
  }

  public void insertRouteInfo(RouteInfo routeInfo) {
    appExecutors.diskIO().execute(() -> routeInfoDao.insertRouteInfo(routeInfo));
  }

  public LiveData<Resource<List<Itinerary>>> loadPlannedTrips(String originLat, String originLon,
                                                              String destLat, String destLon) {
    MutableLiveData<Resource<List<Itinerary>>> resource = new MutableLiveData<>();
    resource.setValue(Resource.loading(null));

    cumtdService.getPlannedTrips(Constants.API_KEY, originLat, originLon, destLat, destLon)
      .enqueue(new Callback<PlannedTripsResponse>() {
        @Override
        public void onResponse(Call<PlannedTripsResponse> call, Response<PlannedTripsResponse> response) {
          if (response.isSuccessful() && response.body() != null) {
            resource.setValue(Resource.success(response.body().getItineraries()));
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
        public void onFailure(Call<PlannedTripsResponse> call, Throwable t) {
          resource.setValue(Resource.error(t.getMessage(), null));
        }
      });

    return resource;
  }
}
