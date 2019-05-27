package com.example.junyoung.culivebus.ui.search;

import com.example.junyoung.culivebus.repository.BusStopRepository;
import com.example.junyoung.culivebus.util.AbsentLiveData;
import com.example.junyoung.culivebus.util.Objects;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.db.entity.StopPoint;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class SearchViewModel extends ViewModel {
  private final BusStopRepository busStopRepository;
  private final MutableLiveData<String> query = new MutableLiveData<>();
  private final LiveData<Resource<List<StopPoint>>> busStops;

  @Inject
  SearchViewModel(BusStopRepository busStopRepository) {
    this.busStopRepository = busStopRepository;
    busStops = Transformations.switchMap(query, search -> {
      Timber.d("Switchmap has been called");
      if (search == null || search.trim().length() == 1) {
        Timber.d("Empty");
        return busStopRepository.loadRecentlySearchedBusStops();
      } else {
        return busStopRepository.searchBusStops(search);
      }
    });
  }

  public LiveData<Resource<List<StopPoint>>> getBusStops() {
    return busStops;
  }

  public void updateBusStop(StopPoint stopPoint) {
    busStopRepository.updateBusStop(stopPoint);
  }

  public void setQuery(@NonNull String originalQuery) {
    String prefixQuery = originalQuery + "*";
    if (Objects.equals(prefixQuery, query.getValue())) {
      return;
    }
    query.setValue(prefixQuery);
  }
}
