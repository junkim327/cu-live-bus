package com.example.junyoung.culivebus.repository;

import com.example.junyoung.culivebus.util.AppExecutors;
import com.example.junyoung.culivebus.api.ApiResponse;
import com.example.junyoung.culivebus.util.Objects;
import com.example.junyoung.culivebus.vo.Resource;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public abstract class NetworkBoundResource<ResultType, RequestType> {
  private final AppExecutors appExecutors;
  private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
  private final MediatorLiveData<Resource<RequestType>> callResult = new MediatorLiveData<>();

  @MainThread
  NetworkBoundResource(AppExecutors appExecutors) {
    this.appExecutors = appExecutors;
    result.setValue(Resource.loading(null));
    LiveData<ResultType> dbSource = loadFromDb();
    result.addSource(dbSource, data -> {
      result.removeSource(dbSource);
      if (shouldFetch(data)) {
        fetchFromNetwork(dbSource);
      } else {
        result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
      }
    });
  }

  @MainThread
  private void setValue(Resource<ResultType> newValue) {
    if (!Objects.equals(result.getValue(), newValue)) {
      result.setValue(newValue);
    }
  }

  private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
    LiveData<ApiResponse<RequestType>> apiResponse = createCall();
    result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
    result.addSource(apiResponse, response -> {
      result.removeSource(apiResponse);
      result.removeSource(dbSource);
      if (response.isSuccessful()) {
        appExecutors.diskIO().execute(() -> {
          saveCallResult(processResponse(response));
          appExecutors.mainThread().execute(() ->
          result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData))));
        });
      } else {
        onFetchFailed();
        result.addSource(dbSource,
          newData ->setValue(Resource.error(response.errorMessage, newData)));
      }
    });
  }

  protected void onFetchFailed() {
  }

  public LiveData<Resource<ResultType>> asLiveData() {
    return result;
  }

  public LiveData<Resource<RequestType>> asCallLiveData() {
    return callResult;
  }

  @WorkerThread
  protected RequestType processResponse(ApiResponse<RequestType> response) {
    return response.body;
  }

  @WorkerThread
  protected abstract void saveCallResult(@NonNull RequestType item);

  @MainThread
  protected abstract boolean shouldFetch(@Nullable ResultType data);

  @NonNull
  @MainThread
  protected abstract LiveData<ResultType> loadFromDb();

  @NonNull
  @MainThread
  protected abstract LiveData<ApiResponse<RequestType>> createCall();
}
