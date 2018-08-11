package com.example.junyoung.uiucbus;

import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.endpoints.BusStopsEndpoints;
import com.example.junyoung.uiucbus.httpclient.pojos.BusStops;

import org.junit.Test;

import static org.junit.Assert.*;

import retrofit2.Call;
import retrofit2.Response;

public class GetEndpointsTest {
  private static final String API_KEY = "72045dffe1964d2282b3b99086a544ec";

  @Test
  public void code_isSuccessful() throws Exception {
    int code = 0;
    BusStopsEndpoints busStopService =
      RetrofitBuilder.getRetrofitInstance().create(BusStopsEndpoints.class);

    Call<BusStops> call = busStopService.getNearestStops(
      API_KEY,
      "40.116566",
      "-88.221959"
    );

    Response<BusStops> response = call.execute();
    System.out.println(call.request().url()
    );
    assertTrue(response.isSuccessful());
  }
}
