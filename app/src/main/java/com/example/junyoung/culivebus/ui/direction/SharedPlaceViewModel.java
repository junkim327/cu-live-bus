package com.example.junyoung.culivebus.ui.direction;

import com.example.junyoung.culivebus.db.entity.RouteInfo;
import com.example.junyoung.culivebus.db.entity.SearchedPlace;
import com.example.junyoung.culivebus.vo.Itinerary;
import com.example.junyoung.culivebus.repository.PlaceRepository;
import com.example.junyoung.culivebus.util.AbsentLiveData;
import com.example.junyoung.culivebus.vo.Resource;
import com.google.android.gms.location.places.Place;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class SharedPlaceViewModel extends ViewModel {
  private final PlaceRepository placeRepository;
  private final LiveData<Resource<List<Itinerary>>> itineraries;
  private final MutableLiveData<Boolean> isStartingPointEditTextClicked = new MutableLiveData<>();
  private final MutableLiveData<String> startingPointName = new MutableLiveData<>();
  private final MutableLiveData<String> destinationName = new MutableLiveData<>();
  private final MutableLiveData<RouteInfo> routeInfo = new MutableLiveData<>();

  @Inject
  SharedPlaceViewModel(PlaceRepository placeRepository) {
    this.placeRepository = placeRepository;

    itineraries = Transformations.switchMap(routeInfo, input -> {
      if (input == null || input.getStartingPointId() == null || input.getDestinationId() == null) {
        Timber.d("Create AbsentLiveData");
        return AbsentLiveData.create();
      } else {
        Timber.d("Load Itineraries, Starting Point : (%s, %s), EndPoint : (%s, %s)", input
          .getStartingPointLat(), input.getStartingPointLon(), input.getDestinationLat(), input
          .getDestinationLon());
        placeRepository.insertRouteInfo(input);
        return placeRepository.loadPlannedTrips(
          String.valueOf(input.getStartingPointLat()),
          String.valueOf(input.getStartingPointLon()),
          String.valueOf(input.getDestinationLat()),
          String.valueOf(input.getDestinationLon())
        );
      }
    });
  }

  public void setIsStartingPointEditTextClicked(boolean isStartingPointEditTextClicked) {
    this.isStartingPointEditTextClicked.setValue(isStartingPointEditTextClicked);
  }

  public LiveData<Resource<List<Itinerary>>> getItineraries() {
    return itineraries;
  }

  public LiveData<Boolean> getIsStartingPointEditTextClicked() {
    return isStartingPointEditTextClicked;
  }

  public LiveData<String> getStartingPointName() {
    return startingPointName;
  }

  public LiveData<String> getDestinationName() {
    return destinationName;
  }

  public void setPlace(@NonNull Place place) {
    if (isStartingPointEditTextClicked.getValue()) {
      Timber.d("Starting Point EditText");
      startingPointName.setValue(place.getName().toString());

      RouteInfo routeInfo = this.routeInfo.getValue();
      if (routeInfo == null) {
        routeInfo = new RouteInfo();
      }
      routeInfo.setStartingPoint(
        place.getId(),
        place.getLatLng().latitude,
        place.getLatLng().longitude,
        place.getName().toString()
      );
      this.routeInfo.setValue(routeInfo);
    } else {
      Timber.d("Destination EditText");
      destinationName.setValue(place.getName().toString());

      RouteInfo routeInfo = this.routeInfo.getValue();
      if (routeInfo == null) {
        routeInfo = new RouteInfo();
      }
      routeInfo.setDestination(
        place.getId(),
        place.getLatLng().latitude,
        place.getLatLng().longitude,
        place.getName().toString()
      );
      this.routeInfo.setValue(routeInfo);
    }
  }

  public void setPlace(@NonNull SearchedPlace searchedPlace) {
    if (isStartingPointEditTextClicked.getValue()) {
      startingPointName.setValue(searchedPlace.getPlaceName());

      RouteInfo routeInfo = this.routeInfo.getValue();
      if (routeInfo == null) {
        routeInfo = new RouteInfo();
      }
      routeInfo.setStartingPoint(
        searchedPlace.getId(),
        searchedPlace.getLatitude(),
        searchedPlace.getLongitude(),
        searchedPlace.getPlaceName()
      );
      this.routeInfo.setValue(routeInfo);
    } else {
      destinationName.setValue(searchedPlace.getPlaceName());

      RouteInfo routeInfo = this.routeInfo.getValue();
      if (routeInfo == null) {
        routeInfo = new RouteInfo();
      }
      routeInfo.setDestination(
        searchedPlace.getId(),
        searchedPlace.getLatitude(),
        searchedPlace.getLongitude(),
        searchedPlace.getPlaceName()
      );
      this.routeInfo.setValue(routeInfo);
    }
  }
}
