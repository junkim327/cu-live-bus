package com.example.junyoung.culivebus.ui.route;

import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.db.entity.Shape;
import com.example.junyoung.culivebus.vo.Status;
import com.example.junyoung.culivebus.vo.StopTime;
import com.example.junyoung.culivebus.httpclient.pojos.Vehicle;
import com.example.junyoung.culivebus.repository.RouteRepository;
import com.example.junyoung.culivebus.repository.StopTimesRepository;
import com.example.junyoung.culivebus.repository.VehicleRepository;
import com.example.junyoung.culivebus.util.AbsentLiveData;
import com.example.junyoung.culivebus.vo.Resource;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

import java.util.List;

import javax.inject.Inject;

public class RouteViewModel extends ViewModel {
  private static final long FORTY_SECONDS = 10_000L;

  private final LiveData<Resource<List<Shape>>> shapeResult;

  private final LiveData<List<Shape>> shapeList;

  private final LiveData<Resource<List<Vehicle>>> vehicleResult;

  private final LiveData<List<Vehicle>> vehicle;

  private final LiveData<Resource<List<StopTime>>> stopTimes;

  private final MutableLiveData<String> shapeId = new MutableLiveData<>();

  private final MutableLiveData<String> tripId = new MutableLiveData<>();

  private final MutableLiveData<String> vehicleId = new MutableLiveData<>();

  private final MutableLiveData<String> headsign = new MutableLiveData<>();

  private final MutableLiveData<String> routeColor = new MutableLiveData<>();

  private final MutableLiveData<ZonedDateTime> vehicleUpdatedTimeInMilli = new MutableLiveData<>();

  private final MutableLiveData<LatLng> latLng = new MutableLiveData<>();

  private final MutableLiveData<Float> zoom = new MutableLiveData<>();

  private final MutableLiveData<Boolean> shouldAnimate = new MutableLiveData<>();

  @Inject
  public RouteViewModel(RouteRepository routeRepository, VehicleRepository vehicleRepository,
                        StopTimesRepository stopTimesRepository) {
    // set initial custom camera attributes
    zoom.setValue(15.0f);
    shouldAnimate.setValue(false);

    shapeResult = Transformations.switchMap(shapeId, id -> {
      if (id == null) {
        return AbsentLiveData.create();
      } else {
        return routeRepository.loadShape(id);
      }
    });

    shapeList = Transformations.map(shapeResult, result -> {
      if (result.status == Status.SUCCESS) {
        return result.data;
      } else {
        return null;
      }
    });

    vehicleResult = Transformations.switchMap(vehicleId, id -> {
      vehicleUpdatedTimeInMilli.setValue(ZonedDateTime.now());
      if (id == null) {
        return AbsentLiveData.create();
      } else {
        return vehicleRepository.getVehicle(id);
      }
    });

    vehicle = Transformations.map(vehicleResult, result -> {
      if (result.status == Status.SUCCESS) {
        return result.data;
      } else {
        return null;
      }
    });

    stopTimes = Transformations.switchMap(tripId, id -> {
      if (id == null) {
        return AbsentLiveData.create();
      } else {
        return stopTimesRepository.getStopTimesByTrip(id);
      }
    });
  }

  /**
   * This function is invoked when the floating action button is clicked.
   *
   * If the vehicle updated time is null, then just request the vehicle location.
   * If the vehicle updated time is not null, then calculate a duration between the vehicle
   * updated time and the current time. If the number of seconds of this duration is over 40
   * seconds, then update the vehicle location.
   */
  public void floatingActionButtonClicked(View view) {
    FloatingActionButton floatingActionButton = (FloatingActionButton) view;
    floatingActionButton.startAnimation(
      AnimationUtils.loadAnimation(
        floatingActionButton.getContext(),
        R.anim.rotate
      )
    );

    if (vehicleUpdatedTimeInMilli.getValue() == null) {
      vehicleId.setValue(vehicleId.getValue());
    } else {
      Duration duration = Duration.between(
        vehicleUpdatedTimeInMilli.getValue(),
        ZonedDateTime.now()
      );
      Timber.d("Duration in millis: %s", duration.toMillis());
      if (duration.toMillis() >= FORTY_SECONDS) {
        vehicleUpdatedTimeInMilli.setValue(ZonedDateTime.now());
        vehicleId.setValue(vehicleId.getValue());
      }
    }
  }

  public void setShapeId(String shapeId) {
    this.shapeId.setValue(shapeId);
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId.setValue(vehicleId);
  }

  public void setTripId(String tripId) {
    this.tripId.setValue(tripId);
  }

  public void setHeadsign(String headsign) {
    this.headsign.setValue(headsign);
  }

  public void setRouteColor(String routeColor) {
    this.routeColor.setValue(routeColor);
  }

  public void setLatLng(LatLng latLng) {
    this.latLng.setValue(latLng);
  }

  public void setZoom(float zoom) {
    this.zoom.setValue(zoom);
  }

  public void setShouldAnimate(boolean shouldAnimate) {
    this.shouldAnimate.setValue(shouldAnimate);
  }

  public LiveData<Resource<List<Shape>>> loadShapeResult() {
    return shapeResult;
  }

  public LiveData<List<Shape>> getShapeList() {
    return shapeList;
  }

  public LiveData<Resource<List<Vehicle>>> loadVehicleResult() {
    return vehicleResult;
  }

  public LiveData<Resource<List<StopTime>>> loadStopTimeResult() {
    return stopTimes;
  }

  public LiveData<String> getVehicleId() {
    return vehicleId;
  }

  public LiveData<String> getHeadsign() {
    return headsign;
  }

  public LiveData<String> getRouteColor() {
    return routeColor;
  }

  public LiveData<LatLng> getLatLng() {
    return latLng;
  }

  public LiveData<Float> getZoom() {
    return zoom;
  }

  public LiveData<Boolean> getShouldAnimate() {
    return shouldAnimate;
  }
}
