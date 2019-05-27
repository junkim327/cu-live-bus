package com.example.junyoung.culivebus.api;

import com.example.junyoung.culivebus.httpclient.pojos.BusStops;
import com.example.junyoung.culivebus.httpclient.pojos.DeparturesByStop;
import com.example.junyoung.culivebus.vo.response.PlannedTripsResponse;
import com.example.junyoung.culivebus.vo.response.StopTimesResponse;
import com.example.junyoung.culivebus.vo.response.VehicleResponse;
import com.example.junyoung.culivebus.vo.response.DeparturesResponse;
import com.example.junyoung.culivebus.vo.response.ShapeResponse;
import com.example.junyoung.culivebus.vo.response.StopsResponse;

import androidx.lifecycle.LiveData;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CumtdService {
  @GET("getvehicle")
  Single<VehicleResponse> getVehicleSingle(@Query("key") String key,
                                           @Query("vehicle_id") String vehicle_id);

  @GET("getdeparturesbystop")
  Observable<DeparturesByStop> getDeparturesByStopObservable(@Query("key") String key,
                                                             @Query("stop_id") String stopId);

  @GET("getdeparturesbystop")
  Call<DeparturesResponse> getDeparturesByStop(@Query("key") String key,
                                                                @Query("stop_id") String stopId);

  @GET("getstops")
  LiveData<ApiResponse<StopsResponse>> getStops(@Query("key") String key);

  @GET("getstopsbylatlon")
  Call<StopsResponse> getStopsByLatLon(@Query("key") String key,
                                       @Query("lat") String lat,
                                       @Query("lon") String lon,
                                       @Query("count") String count);

  @GET("getstopsbysearch")
  Call<BusStops> getStopsBySearch(@Query("key") String key,
                                  @Query("query") String search);

  @GET("getshape")
  LiveData<ApiResponse<ShapeResponse>> getShapes(@Query("key") String key,
                                                 @Query("shape_id") String shapeId,
                                                 @Query("changeset_id") String changesetId);

  @GET("getvehicle")
  Call<VehicleResponse> getVehicle(@Query("key") String key,
                                       @Query("vehicle_id") String vehicle_id);

  @GET("getstoptimesbytrip")
  Call<StopTimesResponse> getStopTimesByTrip(@Query("key") String key,
                                             @Query("trip_id") String tripId);

  @GET("getplannedtripsbylatlon")
  Call<PlannedTripsResponse> getPlannedTrips(@Query("key") String key,
                                             @Query("origin_lat") String originLat,
                                             @Query("origin_lon") String originLon,
                                             @Query("destination_lat") String destinationLat,
                                             @Query("destination_lon") String destinationLon);
}
