package com.example.junyoung.culivebus;

import com.example.junyoung.culivebus.httpclient.RetrofitBuilder;
import com.example.junyoung.culivebus.httpclient.services.BusStopService;
import com.example.junyoung.culivebus.httpclient.services.DepartureService;
import com.example.junyoung.culivebus.httpclient.pojos.BusStops;
import com.example.junyoung.culivebus.httpclient.pojos.DeparturesByStop;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

import retrofit2.Call;
import retrofit2.Response;

public class GetEndpointsTest {
  private static final String CAMPUS_CIRCLE_STOP_ID = "CAMPUSCIR:2";
  private static final String API_KEY = "72045dffe1964d2282b3b99086a544ec";

  @Test
  public void response_isSuccessful() throws Exception {
    BusStopService busStopService =
      RetrofitBuilder.getRetrofitInstance().create(BusStopService.class);

    Call<BusStops> call = busStopService.getNearestStops(
      API_KEY,
      "40.116566",
      "-88.221959",
      "10"
    );

    Response<BusStops> response = call.execute();
    System.out.println(call.request().url());
    assertTrue(response.isSuccessful());
  }

  @Test
  public void code_isSuccessful() throws Exception {
    BusStopService busStopService =
      RetrofitBuilder.getRetrofitInstance().create(BusStopService.class);

    Call<BusStops> call = busStopService.getNearestStops(
      API_KEY,
      "40.116566",
      "-88.221959",
      "10"
    );

    Response<BusStops> response = call.execute();

    int code = 0;
    if (response.isSuccessful()) {
      code = response.body().getStatus().getCode();
    }
    assertEquals(200, code);
  }

  // *************************************************
  // ********** Departure Endpoints Test ************
  // *************************************************

  @Test
  public void departures_response_isSuccessful() throws Exception {
    DepartureService departuresService =
      RetrofitBuilder.getRetrofitInstance().create(DepartureService.class);

    Call<DeparturesByStop> call = departuresService.getDeparturesByStop1(
      API_KEY,
      CAMPUS_CIRCLE_STOP_ID
    );

    Response<DeparturesByStop> response = call.execute();
    System.out.println(response.body().getDepartures().get(0).getTrip().getTripId());
    assertTrue(response.isSuccessful());
  }

  @Test
  public void bus_arrival_time_test() throws Exception {
    DepartureService departuresService =
      RetrofitBuilder.getRetrofitInstance().create(DepartureService.class);

    Call<DeparturesByStop> call = departuresService.getDeparturesByStop1(
      API_KEY,
      CAMPUS_CIRCLE_STOP_ID
    );

    String expected = call.execute().body().getDepartures().get(0).getExpected();
    System.out.println(expected);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
    System.out.println(dateFormat.format(new Date()));

    Date date1 = dateFormat.parse(expected);
    double timeDifference = date1.getTime() - new Date().getTime();
    System.out.println(date1.getTime() - new Date().getTime());
    System.out.println(timeDifference / 1000 % 60);
    System.out.println((int) timeDifference / (60 * 1000) % 60);
  }
}
