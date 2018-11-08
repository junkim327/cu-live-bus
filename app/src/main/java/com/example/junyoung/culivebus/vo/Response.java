package com.example.junyoung.culivebus.vo;

import com.example.junyoung.culivebus.util.Status;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

import static com.example.junyoung.culivebus.util.Status.ERROR;
import static com.example.junyoung.culivebus.util.Status.LOADING;
import static com.example.junyoung.culivebus.util.Status.SUCCESS;

public class Response<T> {
  public final Status mStatus;
  @Nullable
  public final T mData;
  @Nullable
  public final Throwable mError;

  private Response(Status status, @Nullable T data, @Nullable Throwable error) {
    mStatus = status;
    mData = data;
    mError = error;
  }

  public static <T> Response<T> loading() {
    return new Response<T>(LOADING, null, null);
  }

  public static <T> Response<T> success(@NonNull T data) {
    return new Response<>(SUCCESS, data, null);
  }

  public static <T> Response<T> error(@NonNull Throwable error) {
    return new Response<>(ERROR, null, error);
  }
}
