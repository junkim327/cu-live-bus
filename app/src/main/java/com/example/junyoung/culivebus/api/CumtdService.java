package com.example.junyoung.culivebus.api;

import com.example.junyoung.culivebus.httpclient.pojos.BusStops;
import com.example.junyoung.culivebus.httpclient.pojos.DeparturesByStop;
import com.example.junyoung.culivebus.httpclient.pojos.StopTimesByTrip;
import com.example.junyoung.culivebus.httpclient.pojos.VehicleData;
import com.example.junyoung.culivebus.vo.response.GetStopsResponse;

import androidx.lifecycle.LiveData;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CumtdService {
  @GET("getvehicle")
  Single<VehicleData> getVehicleSingle(@Query("key") String key,
                                       @Query("vehicle_id") String vehicle_id);

  @GET("getdeparturesbystop")
  Observable<DeparturesByStop> getDeparturesByStop(@Query("key") String key,
                                                   @Query("stop_id") String stopId);

  @GET("getdeparturesbystop")
  Call<DeparturesByStop> getDeparturesByStop1(@Query("key") String key,
                                              @Query("stop_id") String stopId);

  @GET("getstopsbylatlon")
  Call<BusStops> getNearestStops(@Query("key") String key,
                                 @Query("lat") String lat,
                                 @Query("lon") String lon,
                                 @Query("count") String count);

  @GET("getstops")
  LiveData<ApiResponse<GetStopsResponse>> getStops(@Query("key") String key);

  @GET("getstopsbysearch")
  Call<BusStops> getStopsBySearch(@Query("key") String key,
                                  @Query("query") String search);

  @GET("getstoptimesbytrip")
  LiveData<ApiResponse<StopTimesByTrip>> getStopTimesByTrip(@Query("key") String key,
                                                            @Query("trip_id") String tripId);
}
