package com.example.junyoung.culivebus.ui.nearbystop;

import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.example.junyoung.culivebus.repository.BusStopRepository;
import com.example.junyoung.culivebus.util.Event;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

public class NearByStopViewModel extends ViewModel {
  private final LiveData<List<StopPoint>> busStops;
  private final MutableLiveData<LatLng> latLng = new MutableLiveData<>();
  private final MutableLiveData<Integer> mapPaddingBottom = new MutableLiveData<>();
  private final MutableLiveData<StopPoint> clickedStopPoint = new MutableLiveData<>();
  private final MutableLiveData<Event<LatLng>> clusterClickEvent = new MutableLiveData<>();
  private final MutableLiveData<Event<Integer>> bottomSheetStateEvent = new MutableLiveData<>();

  @Inject
  NearByStopViewModel(BusStopRepository busStopRepository) {
    busStops = busStopRepository.loadAllBusStops();
  }

  public void onClusterItemClick(StopPoint stopPoint) {
    clickedStopPoint.setValue(stopPoint);
    bottomSheetStateEvent.setValue(new Event<>(STATE_EXPANDED));
  }

  public LiveData<List<StopPoint>> getBusStops() {
    return busStops;
  }

  public LiveData<LatLng> getLatLng() {
    return latLng;
  }

  public LiveData<Integer> getMapPaddingBottom() {
    return mapPaddingBottom;
  }

  public LiveData<StopPoint> getClickedStopPoint() {
    return clickedStopPoint;
  }

  public LiveData<Event<LatLng>> getClusterClickEvent() {
    return clusterClickEvent;
  }

  public LiveData<Event<Integer>> getBottomSheetStateEvent() {
    return bottomSheetStateEvent;
  }

  public void setLatLng(LatLng latLng) {
    this.latLng.setValue(latLng);
  }

  public void setMapPaddingBottom(int mapBottomPadding) {
    this.mapPaddingBottom.setValue(mapBottomPadding);
  }

  public void setClusterClickEvent(LatLng clusterLatLng) {
    clusterClickEvent.setValue(new Event<>(clusterLatLng));
  }
}
