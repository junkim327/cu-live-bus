package com.example.junyoung.culivebus.httpclient.services;

import com.example.junyoung.culivebus.vo.response.VehicleResponse;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BusLocationService {
  @GET("getvehicle")
  Call<VehicleResponse> getBusLocation(@Query("key") String key,
                                       @Query("vehicle_id") String vehicle_id);

  @GET("getvehicle")
  Observable<VehicleResponse> getVehicleObservable(@Query("key") String key,
                                                   @Query("vehicle_id") String vehicle_id);

  @GET("getvehicle")
  Single<VehicleResponse> getVehicleSingle(@Query("key") String key,
                                           @Query("vehicle_id") String vehicle_id);
}
