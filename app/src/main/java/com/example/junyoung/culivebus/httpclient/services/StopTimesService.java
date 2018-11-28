package com.example.junyoung.culivebus.httpclient.services;

import com.example.junyoung.culivebus.httpclient.pojos.StopTimesByTrip;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StopTimesService {
  @GET("getstoptimesbytrip")
  Call<StopTimesByTrip> getStopTimesByTrip(@Query("key") String key,
                                           @Query("trip_id") String tripId);
}
