package com.example.junyoung.culivebus.httpclient.repository;

import android.arch.lifecycle.MutableLiveData;

import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.httpclient.RetrofitBuilder;
import com.example.junyoung.culivebus.httpclient.pojos.Leg;
import com.example.junyoung.culivebus.httpclient.pojos.Path;
import com.example.junyoung.culivebus.httpclient.pojos.Service;
import com.example.junyoung.culivebus.httpclient.services.ShapeService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusPathRepository {
  private ShapeService mShapeService;

  public BusPathRepository() {
    mShapeService = RetrofitBuilder.getRetrofitandRxJavaInstance()
      .create(ShapeService.class);
  }

  public Single<List<Path>> getBusPathList(List<Leg> busList) {
    List<Single<Path>> pathSources = new ArrayList<>();
    for (Leg busService : busList) {
      Service service = busService.getServices().get(0);
      Single<Path> pathSource = mShapeService.getShapeBetweenStops(
        Constants.API_KEY,
        service.getBegin().getStopId(),
        service.getEnd().getStopId(),
        service.getTrip().getShapeId()
      );
      pathSources.add(pathSource.subscribeOn(Schedulers.io()));
    }

    return Single.zip(pathSources, objects -> {
      final List<Path> pathList = new ArrayList<>();
      for (Object obj : objects) {
        pathList.add((Path) obj);
      }
      return pathList;
    });
  }

  public MutableLiveData<Path> getBusPath(MutableLiveData<Path> path, String shapeId) {
    mShapeService.getShape(Constants.API_KEY, shapeId).enqueue(new Callback<Path>() {
      @Override
      public void onResponse(Call<Path> call, Response<Path> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            path.setValue(response.body());
          }
        }
      }

      @Override
      public void onFailure(Call<Path> call, Throwable t) {

      }
    });

    return path;
  }


}
