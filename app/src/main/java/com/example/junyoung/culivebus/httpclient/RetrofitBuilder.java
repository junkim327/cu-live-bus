package com.example.junyoung.culivebus.httpclient;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
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

      public static Retrofit getRetrofitandRxJavaInstance() {
        Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(BASE_URL)
          .addConverterFactory(GsonConverterFactory.create())
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .build();

    return retrofit;
  }
}
