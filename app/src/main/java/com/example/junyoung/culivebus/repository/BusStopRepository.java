package com.example.junyoung.culivebus.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.example.junyoung.culivebus.db.dao.FavoriteStopDao;
import com.example.junyoung.culivebus.util.AppExecutors;
import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.api.ApiResponse;
import com.example.junyoung.culivebus.api.CumtdService;
import com.example.junyoung.culivebus.db.CuLiveBusDb;
import com.example.junyoung.culivebus.db.dao.BusStopDao;
import com.example.junyoung.culivebus.httpclient.pojos.Stop;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.example.junyoung.culivebus.vo.response.StopsResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.Transformations;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class BusStopRepository {
  private final CuLiveBusDb db;
  private final BusStopDao busStopDao;
  private final CumtdService cumtdService;
  private final AppExecutors appExecutors;

  @Inject
  public BusStopRepository(CuLiveBusDb db, BusStopDao busStopDao,
                           CumtdService cumtdService, AppExecutors appExecutors) {
    this.db = db;
    this.busStopDao = busStopDao;
    this.cumtdService = cumtdService;
    this.appExecutors = appExecutors;
  }

  public LiveData<Resource<Integer>> insertBusStops(String apiKey) {
    return new NetworkBoundResource<Integer, StopsResponse>(appExecutors) {
      @Override
      protected void saveCallResult(@NonNull StopsResponse item) {
        List<StopPoint> stopPoints = new ArrayList<>();
        for (Stop stop : item.getStops()) {
          stopPoints.addAll(stop.getStopPoints());
        }
        busStopDao.insertAll(stopPoints);
      }

      @Override
      protected boolean shouldFetch(@Nullable Integer data) {
        return data == 0;
      }

      @NonNull
      @Override
      protected LiveData<Integer> loadFromDb() {
        return busStopDao.countBusStops();
      }

      @NonNull
      @Override
      protected LiveData<ApiResponse<StopsResponse>> createCall() {
        return cumtdService.getStops(apiKey);
      }
    }.asLiveData();
  }

  public LiveData<List<StopPoint>> loadAllBusStops() {
    return busStopDao.loadAllBusStops();
  }

  public LiveData<Resource<List<StopPoint>>> loadRecentlySearchedBusStops() {
    return Transformations.map(busStopDao.loadRecentlySearchedBusStops(), Resource::success);
  }
  
  public void updateBusStop(StopPoint stopPoint) {
    appExecutors.diskIO().execute(() -> {
      busStopDao.updateBusStop(stopPoint);
    });
  }

  public LiveData<Resource<List<StopPoint>>> searchBusStops(String query) {
    return new NetworkBoundResource<List<StopPoint>, List<StopPoint>>(appExecutors) {
      @Override
      protected void saveCallResult(@NonNull List<StopPoint> item) {
        // Do nothing
      }

      @Override
      protected boolean shouldFetch(@Nullable List<StopPoint> data) {
        return false;
      }

      @NonNull
      @Override
      protected LiveData<List<StopPoint>> loadFromDb() {
        return busStopDao.searchAllBusStops(query);
      }

      @NonNull
      @Override
      protected LiveData<ApiResponse<List<StopPoint>>> createCall() {
        return null;
      }
    }.asLiveData();
  }
}
