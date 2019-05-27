package com.example.junyoung.culivebus.ui.departure;

import android.view.View;
import android.widget.ToggleButton;

import com.example.junyoung.culivebus.db.entity.FavoriteStop;
import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.example.junyoung.culivebus.httpclient.pojos.Departure;
import com.example.junyoung.culivebus.vo.SortedDeparture;
import com.example.junyoung.culivebus.repository.DepartureRepository;
import com.example.junyoung.culivebus.repository.FavoriteStopRepository;
import com.example.junyoung.culivebus.util.Event;
import com.example.junyoung.culivebus.vo.Resource;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class DepartureViewModel extends ViewModel {
  private static final long FORTY_SECONDS = 40_000L;
  private static final String ADDED_TO_FAVORITES = "Added to Favorites";
  private static final String REMOVED_FROM_FAVORITES = "Removed from Favorites";

  private Timer timer;
  private String stopId;
  private final FavoriteStopRepository favoriteStopRepository;
  private final DepartureRepository departureRepository;
  private final LiveData<List<String>> stopIdList;
  private final MutableLiveData<StopPoint> stopPoint = new MutableLiveData<>();
  private final MutableLiveData<Boolean> isMapLoaded = new MutableLiveData<>();
  private final MutableLiveData<Event<Boolean>> resumed = new MutableLiveData<>();
  private final MutableLiveData<Event<String>> toastMessage = new MutableLiveData<>();
  private final MutableLiveData<Event<Departure>> clickedDeparture = new MutableLiveData<>();
  private final MutableLiveData<Resource<List<SortedDeparture>>> departures =
    new MutableLiveData<>();

  @Inject
  DepartureViewModel(FavoriteStopRepository favoriteStopRepository,
                     DepartureRepository departureRepository) {
    this.favoriteStopRepository = favoriteStopRepository;
    this.departureRepository = departureRepository;

    isMapLoaded.setValue(false);
    stopIdList = favoriteStopRepository.loadStopIdList();
  }

  public void setStopPoint(StopPoint stopPoint) {
    this.stopPoint.setValue(stopPoint);
  }

  public void setStopId(String stopId) {
    this.stopId = stopId;
    //initTimer();
  }

  public void setResumed(boolean resumed) {
    this.resumed.setValue(new Event<>(resumed));
  }

  public LiveData<Event<Boolean>> isResumed() {
    return resumed;
  }

  public LiveData<StopPoint> getStopPoint() {
    return stopPoint;
  }

  public LiveData<List<String>> getStopIdList() {
    if (stopIdList.getValue() != null) {
      Timber.d("Size : %s", stopIdList.getValue().size());
    }
    return stopIdList;
  }

  public LiveData<Boolean> getIsMapLoaded() {
    return isMapLoaded;
  }

  public LiveData<Resource<List<SortedDeparture>>> getDepartures() {
    return departures;
  }

  public LiveData<Event<String>> getToastMessage() {
    return toastMessage;
  }

  public LiveData<Event<Departure>> getClickedDeparture() {
    return clickedDeparture;
  }

  public void setIsMapLoaded(boolean isMapLoaded) {
    Timber.d("isMapLoaded called");
    this.isMapLoaded.setValue(isMapLoaded);
  }

  public void favoriteButtonClicked(View view, StopPoint stopPoint) {
    if (stopPoint != null) {
      if (((ToggleButton) view).isChecked()) {
        Timber.d("Checked");
        toastMessage.setValue(new Event<>(ADDED_TO_FAVORITES));
        favoriteStopRepository.insertFavoriteStop(
          new FavoriteStop(
            stopPoint.getStopId(),
            stopPoint.getStopCode(),
            stopPoint.getStopName()
          )
        );
      } else {
        Timber.d("UnChecked");
        toastMessage.setValue(new Event<>(REMOVED_FROM_FAVORITES));
        favoriteStopRepository.deleteFavoriteStop(stopPoint.getStopId());
      }
    }
  }

  public void departureClicked(SortedDeparture sortedDeparture) {
    Departure departure = new Departure(
      sortedDeparture.getTripList().get(0),
      sortedDeparture.getRouteList().get(0),
      sortedDeparture.getHeadSign(),
      sortedDeparture.getExpectedList().get(0),
      sortedDeparture.getVehicleIdList().get(0)
    );
    clickedDeparture.setValue(new Event<>(departure));
  }

  public void initTimer() {
    if (stopPoint.getValue() != null && stopPoint.getValue().getStopId() != null) {
      timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          departureRepository.getDeparturesByStop(stopPoint.getValue().getStopId(), departures);
        }
      }, 0, FORTY_SECONDS);
    }
  }

  public void cancelTimer() {
    if (timer != null) {
      Timber.d("Timer cancel");
      timer.cancel();
      timer = null;
    }
  }

  public void resetTimer() {
    cancelTimer();
    initTimer();
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    Timber.d("onCleared called");
    cancelTimer();
  }
}
