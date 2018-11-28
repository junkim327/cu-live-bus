package com.example.junyoung.culivebus.httpclient.services;

import com.example.junyoung.culivebus.httpclient.pojos.DeparturesByStop;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DepartureService {
  @GET("getdeparturesbystop")
  Call<DeparturesByStop> getDeparturesByStop1(@Query("key") String key,
                                              @Query("stop_id") String stopId);

  @GET("getdeparturesbystop")
  Observable<DeparturesByStop> getDeparturesByStop(@Query("key") String key,
                                                   @Query("stop_id") String stopId);

  @GET("getdeparturesbystop")
  Flowable<DeparturesByStop> getDeparturesByStop2(@Query("key") String key,
                                                  @Query("stop_id") String stopId);
}
