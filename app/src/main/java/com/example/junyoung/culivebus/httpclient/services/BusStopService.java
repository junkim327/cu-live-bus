package com.example.junyoung.culivebus.httpclient.services;

import com.example.junyoung.culivebus.httpclient.pojos.BusStops;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BusStopService {
  @GET("getstopsbylatlon")
  Call<BusStops> getNearestStops(@Query("key") String key,
                                 @Query("lat") String lat,
                                 @Query("lon") String lon,
                                 @Query("count") String count);

  @GET("getstopsbysearch")
  Call<BusStops> getStopsBySearch(@Query("key") String key,
                                  @Query("query") String search);
}
