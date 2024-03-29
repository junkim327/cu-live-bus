package com.example.junyoung.culivebus.vo;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

import static com.example.junyoung.culivebus.vo.Status.ERROR;
import static com.example.junyoung.culivebus.vo.Status.LOADING;
import static com.example.junyoung.culivebus.vo.Status.SUCCESS;

public class Resource<T> {
  @NonNull
  public final Status status;
  @Nullable
  public final T data;
  @Nullable
  public final String message;

  private Resource(Status status, @Nullable T data, @Nullable String message) {
    this.status = status;
    this.data = data;
    this.message = message;
  }

  public static <T> Resource<T> success(@NonNull T data) {
    return new Resource<>(SUCCESS, data, null);
  }

  public static <T> Resource<T> error(String msg, @Nullable T data) {
    return new Resource<>(ERROR, data, msg);
  }

  public static <T> Resource<T> loading(@Nullable T data) {
    return new Resource<>(LOADING, data, null);
  }
}
