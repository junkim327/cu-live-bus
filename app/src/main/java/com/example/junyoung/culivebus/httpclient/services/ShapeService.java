package com.example.junyoung.culivebus.httpclient.services;

import com.example.junyoung.culivebus.httpclient.pojos.Path;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ShapeService {
  @GET("getshape")
  Call<Path> getShape(@Query("key") String key,
                      @Query("shape_id") String shapeId);

  @GET("getshapebetweenstops")
  Single<Path> getShapeBetweenStops(@Query("key") String key,
                                    @Query("begin_stop_id") String beginStopId,
                                    @Query("end_stop_id") String endStopId,
                                    @Query("shape_id") String shapeId);
}
