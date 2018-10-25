package com.example.junyoung.uiucbus.httpclient.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.junyoung.uiucbus.Constants;
import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.pojos.Path;
import com.example.junyoung.uiucbus.httpclient.services.ShapeService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusPathRepository {
  private ShapeService mShapeService;

  public BusPathRepository() {
    mShapeService = RetrofitBuilder.getRetrofitandRxJavaInstance()
      .create(ShapeService.class);
  }

  public LiveData<Path> getBusPath(String shapeId) {
    final MutableLiveData<Path> data = new MutableLiveData<>();
    mShapeService.getShape(Constants.API_KEY, shapeId).enqueue(new Callback<Path>() {
      @Override
      public void onResponse(Call<Path> call, Response<Path> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            data.setValue(response.body());
          }
        }
      }

      @Override
      public void onFailure(Call<Path> call, Throwable t) {

      }
    });

    return data;
  }
}
