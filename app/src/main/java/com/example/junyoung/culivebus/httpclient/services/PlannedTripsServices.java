package com.example.junyoung.culivebus.httpclient.services;

import com.example.junyoung.culivebus.httpclient.pojos.PlannedTrips;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlannedTripsServices {
  @GET("getplannedtripsbylatlon")
  Single<PlannedTrips> getPlannedTrips(@Query("key") String key,
                                       @Query("origin_lat") String originLat,
                                       @Query("origin_lon") String originLon,
                                       @Query("destination_lat") String destinationLat,
                                       @Query("destination_lon") String destinationLon);
}
