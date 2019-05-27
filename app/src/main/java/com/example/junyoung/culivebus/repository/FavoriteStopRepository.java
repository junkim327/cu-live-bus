package com.example.junyoung.culivebus.repository;

import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.api.CumtdService;
import com.example.junyoung.culivebus.httpclient.pojos.Departure;
import com.example.junyoung.culivebus.httpclient.pojos.DeparturesByStop;
import com.example.junyoung.culivebus.httpclient.pojos.Status;
import com.example.junyoung.culivebus.util.AppExecutors;
import com.example.junyoung.culivebus.db.CuLiveBusDb;
import com.example.junyoung.culivebus.db.dao.FavoriteStopDao;
import com.example.junyoung.culivebus.db.entity.FavoriteStop;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.vo.SortedDeparture;
import com.example.junyoung.culivebus.vo.SortedFavoriteDeparture;
import com.example.junyoung.culivebus.vo.response.DeparturesResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class FavoriteStopRepository {
  private final CuLiveBusDb db;
  private final FavoriteStopDao favoriteStopDao;
  private final CumtdService cumtdService;
  private final AppExecutors appExecutors;

  @Inject
  public FavoriteStopRepository(CuLiveBusDb db, FavoriteStopDao favoriteStopDao,
                                CumtdService cumtdService, AppExecutors appExecutors) {
    this.db = db;
    this.favoriteStopDao = favoriteStopDao;
    this.cumtdService = cumtdService;
    this.appExecutors = appExecutors;
  }

  public void insertFavoriteStop(FavoriteStop favoriteStop) {
    appExecutors.diskIO().execute(() -> {
      favoriteStopDao.insertFavoriteStop(favoriteStop);
    });
  }

  public LiveData<List<FavoriteStop>> loadAllFavoriteStops() {
    return favoriteStopDao.loadAllFavoriteStops();
  }

  public LiveData<List<String>> loadStopIdList() {
    return favoriteStopDao.loadStopIdList();
  }

  public void deleteFavoriteStop(String stopId) {
    appExecutors.diskIO().execute(() -> {
      favoriteStopDao.deleteFavoriteStopByStopId(stopId);
    });
  }

  public void getDeparturesByStop(FavoriteStop favoriteStop,
                                  MutableLiveData<Resource<List<SortedFavoriteDeparture>>> departures) {
    departures.postValue(Resource.loading(null));
    cumtdService.getDeparturesByStop(Constants.API_KEY, favoriteStop.getStopId())
      .enqueue(new Callback<DeparturesResponse>() {
      @Override
      public void onResponse(Call<DeparturesResponse> call, Response<DeparturesResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
          Timber.d("Departure Size : %s", response.body().getDepartures().size());
          List<SortedFavoriteDeparture> favoriteDepartures = new ArrayList<>();
          favoriteDepartures.add(new SortedFavoriteDeparture(favoriteStop));

          List<SortedDeparture> sortedDepartures = sortDepartures(response.body().getDepartures());
          for (SortedDeparture sortedDeparture : sortedDepartures) {
            favoriteDepartures.add(new SortedFavoriteDeparture(sortedDeparture));
          }

          departures.setValue(Resource.success(favoriteDepartures));
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

  public DisposableObserver<List<SortedFavoriteDeparture>> getTwoFavoriteStopDepartures(
    List<FavoriteStop> favoriteStops,
    MutableLiveData<Boolean> isLoading,
    MutableLiveData<Resource<List<SortedFavoriteDeparture>>> departures) {
    Timber.d("Starting to create the observer");
    return Observable.zip(
      cumtdService.getDeparturesByStopObservable(
        Constants.API_KEY,
        favoriteStops.get(0).getStopId()
      ).onErrorReturn(new Function<Throwable, DeparturesByStop>() {
        @Override
        public DeparturesByStop apply(Throwable throwable) throws Exception {
          Timber.d("onErrorReturn: %s", throwable.toString());
          return new DeparturesByStop(new Status(Constants.ZIP_ERROR_STATUS_CODE));
        }
      }),
      cumtdService.getDeparturesByStopObservable(
        Constants.API_KEY,
        favoriteStops.get(1).getStopId()
      ).onErrorReturn(new Function<Throwable, DeparturesByStop>() {
        @Override
        public DeparturesByStop apply(Throwable throwable) throws Exception {
          Timber.d("onErrorReturn: %s", throwable.toString());
          return new DeparturesByStop(new Status(Constants.ZIP_ERROR_STATUS_CODE));
        }
      }),
      new BiFunction<DeparturesByStop, DeparturesByStop, List<SortedFavoriteDeparture>>() {
        @Override
        public List<SortedFavoriteDeparture> apply(DeparturesByStop departuresByStop,
                                                   DeparturesByStop departuresByStop2) {
          List<SortedFavoriteDeparture> favoriteDepartures = new ArrayList<>();
          List<DeparturesByStop> departuresByStopList = new ArrayList<>();
          departuresByStopList.add(departuresByStop);
          departuresByStopList.add(departuresByStop2);

          Timber.d("Starting to sort departures");
          for (int i = 0; i < 2; i++) {
            favoriteDepartures.add(new SortedFavoriteDeparture(favoriteStops.get(i)));
            List<SortedDeparture> sortedDepartures = sortDepartures(
              departuresByStopList.get(i).getDepartures()
            );
            for (SortedDeparture sortedDeparture : sortedDepartures) {
              favoriteDepartures.add(new SortedFavoriteDeparture(sortedDeparture));
            }
          }

          return favoriteDepartures;
        }
      }
    ).subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeWith(new DisposableObserver<List<SortedFavoriteDeparture>>() {
        @Override
        public void onNext(List<SortedFavoriteDeparture> favoriteDepartures) {
          departures.setValue(Resource.success(favoriteDepartures));
        }

        @Override
        public void onError(Throwable e) {
          Timber.d("onError");
          isLoading.setValue(false);
        }

        @Override
        public void onComplete() {
          Timber.d("onComplete");
          isLoading.setValue(false);
        }
      });
  }

  public DisposableObserver<List<SortedFavoriteDeparture>> getThreeFavoriteStopDepartures(
    List<FavoriteStop> favoriteStops,
    MutableLiveData<Boolean> isLoading,
    MutableLiveData<Resource<List<SortedFavoriteDeparture>>> departures) {
    Timber.d("Starting to create the observer");
    return Observable.zip(
      cumtdService.getDeparturesByStopObservable(
        Constants.API_KEY,
        favoriteStops.get(0).getStopId()
      ).onErrorReturn(new Function<Throwable, DeparturesByStop>() {
        @Override
        public DeparturesByStop apply(Throwable throwable) throws Exception {
          Timber.d("onErrorReturn: %s", throwable.toString());
          return new DeparturesByStop(new Status(Constants.ZIP_ERROR_STATUS_CODE));
        }
      }),
      cumtdService.getDeparturesByStopObservable(
        Constants.API_KEY,
        favoriteStops.get(1).getStopId()
      ).onErrorReturn(new Function<Throwable, DeparturesByStop>() {
        @Override
        public DeparturesByStop apply(Throwable throwable) throws Exception {
          Timber.d("onErrorReturn: %s", throwable.toString());
          return new DeparturesByStop(new Status(Constants.ZIP_ERROR_STATUS_CODE));
        }
      }),
      cumtdService.getDeparturesByStopObservable(
        Constants.API_KEY,
        favoriteStops.get(2).getStopId()
      ).onErrorReturn(new Function<Throwable, DeparturesByStop>() {
        @Override
        public DeparturesByStop apply(Throwable throwable) throws Exception {
          Timber.d("onErrorReturn: %s", throwable.toString());
          return new DeparturesByStop(new Status(Constants.ZIP_ERROR_STATUS_CODE));
        }
      }),
      new Function3<DeparturesByStop, DeparturesByStop, DeparturesByStop, List<SortedFavoriteDeparture>>() {
        @Override
        public List<SortedFavoriteDeparture> apply(DeparturesByStop departuresByStop,
                                                   DeparturesByStop departuresByStop2,
                                                   DeparturesByStop departuresByStop3) {
          List<SortedFavoriteDeparture> favoriteDepartures = new ArrayList<>();
          List<DeparturesByStop> departuresByStopList = new ArrayList<>();
          departuresByStopList.add(departuresByStop);
          departuresByStopList.add(departuresByStop2);
          departuresByStopList.add(departuresByStop3);

          Timber.d("Starting to sort departures");
          for (int i = 0; i < 3; i++) {
            favoriteDepartures.add(new SortedFavoriteDeparture(favoriteStops.get(i)));
            List<SortedDeparture> sortedDepartures = sortDepartures(
              departuresByStopList.get(i).getDepartures()
            );
            for (SortedDeparture sortedDeparture : sortedDepartures) {
              favoriteDepartures.add(new SortedFavoriteDeparture(sortedDeparture));
            }
          }

          return favoriteDepartures;
        }
      }
    ).subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeWith(new DisposableObserver<List<SortedFavoriteDeparture>>() {
        @Override
        public void onNext(List<SortedFavoriteDeparture> favoriteDepartures) {
          departures.setValue(Resource.success(favoriteDepartures));
        }

        @Override
        public void onError(Throwable e) {
          Timber.d("onError");
          isLoading.setValue(false);
        }

        @Override
        public void onComplete() {
          Timber.d("onComplete");
          isLoading.setValue(false);
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
