package com.example.junyoung.culivebus.ui.dashboard;

import com.example.junyoung.culivebus.db.entity.FavoriteStop;
import com.example.junyoung.culivebus.repository.DepartureRepository;
import com.example.junyoung.culivebus.repository.FavoriteStopRepository;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.vo.SortedDeparture;
import com.example.junyoung.culivebus.vo.SortedFavoriteDeparture;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class DashboardViewModel extends ViewModel {
  private final DepartureRepository departureRepository;
  private final FavoriteStopRepository favoriteStopRepository;
  private final LiveData<List<FavoriteStop>> favoriteStops;
  private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
  private final MutableLiveData<Resource<List<SortedFavoriteDeparture>>> favoriteDepartures =
    new MutableLiveData<>();
  private final CompositeDisposable disposable = new CompositeDisposable();

  @Inject
  DashboardViewModel(DepartureRepository departureRepository,
                     FavoriteStopRepository favoriteStopRepository) {
    this.departureRepository = departureRepository;
    this.favoriteStopRepository = favoriteStopRepository;
    favoriteStops = favoriteStopRepository.loadAllFavoriteStops();
  }

  public LiveData<Boolean> getIsLoading() {
    return isLoading;
  }

  public LiveData<List<FavoriteStop>> getFavoriteStops() {
    return favoriteStops;
  }

  public LiveData<Resource<List<SortedFavoriteDeparture>>> getFavoriteDepartures() {
    return favoriteDepartures;
  }

  public void getFavoriteStopsDepartures(List<FavoriteStop> favoriteStops) {
    disposable.add(
      Observable.interval(0, 40, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(seconds -> {
          isLoading.setValue(true);
        })
        .observeOn(Schedulers.computation())
        .subscribeWith(new DisposableObserver<Long>() {
          @Override
          public void onNext(Long aLong) {
            Timber.d("%s seconds", aLong);
            if (favoriteStops.size() == 1) {
              Timber.d("onNext: size 1");
              favoriteStopRepository.getDeparturesByStop(
                favoriteStops.get(0),
                favoriteDepartures
              );
            } else if (favoriteStops.size() == 2) {
              Timber.d("onNext: size 2");
              disposable.add(
                favoriteStopRepository.getTwoFavoriteStopDepartures(
                  favoriteStops,
                  isLoading,
                  favoriteDepartures
                )
              );
            } else if (favoriteStops.size() == 3) {
              Timber.d("onNext: size 3");
              disposable.add(
                favoriteStopRepository.getThreeFavoriteStopDepartures(
                  favoriteStops,
                  isLoading,
                  favoriteDepartures
                )
              );
            }
          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onComplete() {
          }
        })
    );
  }

  @Override
  protected void onCleared() {
    Timber.d("onCleared");
    super.onCleared();
    if (!disposable.isDisposed()) {
      disposable.dispose();
    }
  }
}
