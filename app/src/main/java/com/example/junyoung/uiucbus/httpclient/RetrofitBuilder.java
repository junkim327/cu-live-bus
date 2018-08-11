package com.example.junyoung.uiucbus.httpclient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {
  private static final String VERSION = "v2.2";
  private static final String FORMAT = "json";
  private static final String BASE_URL =
    "https://developer.cumtd.com/api/" + VERSION + "/" + FORMAT + "/";

  public static Retrofit getRetrofitInstance() {
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build();

    return retrofit;
  }
}
