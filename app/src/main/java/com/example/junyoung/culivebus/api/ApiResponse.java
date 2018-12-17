package com.example.junyoung.culivebus.api;

import java.io.IOException;

import io.reactivex.annotations.Nullable;
import retrofit2.Response;
import timber.log.Timber;

public class ApiResponse<T> {
  public final int code;
  @Nullable
  public final T body;
  @Nullable
  public final String errorMessage;

  public ApiResponse(Throwable error) {
    code = 500;
    body = null;
    errorMessage = error.getMessage();
  }

  public ApiResponse(Response<T> response) {
    code = response.code();
    if (response.isSuccessful()) {
      body = response.body();
      errorMessage = null;
    } else {
      String message = null;
      if (response.errorBody() != null) {
        try {
          message = response.errorBody().string();
        } catch (IOException ignored) {
          Timber.e(ignored, "error while parsing response");
        }
      }

      if (message == null || message.trim().length() == 0) {
        message = response.message();
      }
      errorMessage = message;
      body = null;
    }
  }

  public boolean isSuccessful() {
    return code == 200;
  }
}
