package com.example.junyoung.culivebus.httpclient.services;

import com.example.junyoung.culivebus.httpclient.pojos.VehicleData;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BusLocationService {
  @GET("getvehicle")
  Call<VehicleData> getBusLocation(@Query("key") String key,
                                   @Query("vehicle_id") String vehicle_id);

  @GET("getvehicle")
  Observable<VehicleData> getVehicleObservable(@Query("key") String key,
                                               @Query("vehicle_id") String vehicle_id);

  @GET("getvehicle")
  Single<VehicleData> getVehicleSingle(@Query("key") String key,
                                       @Query("vehicle_id") String vehicle_id);
}
