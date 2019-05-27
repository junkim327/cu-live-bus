package com.example.junyoung.culivebus.ui.direction.search;

import com.example.junyoung.culivebus.db.entity.SearchedPlace;
import com.example.junyoung.culivebus.repository.PlaceRepository;
import com.example.junyoung.culivebus.vo.Resource;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class SearchHistoryViewModel extends ViewModel {
  private final LiveData<Resource<List<SearchedPlace>>> searchedPlaces;

  @Inject
  SearchHistoryViewModel(PlaceRepository placeRepository) {
    searchedPlaces = placeRepository.loadAllSearchedPlaces();
  }

  public LiveData<Resource<List<SearchedPlace>>> getSearchedPlaces() {
    return searchedPlaces;
  }
}
