package com.example.junyoung.culivebus.ui.direction.search;

import com.example.junyoung.culivebus.db.entity.SearchedPlace;
import com.example.junyoung.culivebus.repository.PlaceRepository;
import com.example.junyoung.culivebus.vo.Resource;
import com.google.android.gms.location.places.Place;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DirectionSearchViewModel extends ViewModel {
  private final PlaceRepository placeRepository;
  private final LiveData<Resource<List<SearchedPlace>>> searchedPlaces;
  private final MutableLiveData<Boolean> areContentsLoaded = new MutableLiveData<>();
  private final MutableLiveData<Integer> resultCount = new MutableLiveData<>();

  @Inject
  DirectionSearchViewModel(PlaceRepository placeRepository) {
    this.placeRepository = placeRepository;
    searchedPlaces = placeRepository.loadAllSearchedPlaces();
  }

  public void setAreContentsLoaded(boolean areContentsLoaded) {
    this.areContentsLoaded.setValue(areContentsLoaded);
  }

  public void setResultCount(int count) {
    this.resultCount.setValue(count);
  }

  public LiveData<Boolean> getAreContentsLoaded() {
    return areContentsLoaded;
  }

  public LiveData<Integer> getResultCount() {
    return resultCount;
  }

  public LiveData<Resource<List<SearchedPlace>>> getSearchedPlaces() {
    return searchedPlaces;
  }

  public void insertSearchedPlace(@NonNull Place place) {
    if (place.getAddress() != null) {
      placeRepository.insertSearchedPlace(
        new SearchedPlace(
          place.getId(),
          place.getLatLng().latitude,
          place.getLatLng().longitude,
          place.getName().toString(),
          trimAddress(place.getAddress().toString())
        )
      );
    } else {
      placeRepository.insertSearchedPlace(
        new SearchedPlace(
          place.getId(),
          place.getLatLng().latitude,
          place.getLatLng().longitude,
          place.getName().toString()
        )
      );
    }
  }

  private String trimAddress(String address) {
    int firstCommaIndex = address.lastIndexOf(',');
    int thirdCommaIndex =
      address.lastIndexOf(',', address.lastIndexOf(',', firstCommaIndex - 1) - 1);

    if (thirdCommaIndex == -1) {
      return address.substring(0, firstCommaIndex);
    } else {
      return address.substring(thirdCommaIndex + 2, firstCommaIndex);
    }
  }
}
