package com.example.junyoung.culivebus.util;

import androidx.lifecycle.LiveData;

public class AbsentLiveData extends LiveData {
  private AbsentLiveData() {
    postValue(null);
  }

  public static <T> LiveData<T> create() {
    return new AbsentLiveData();
  }
}
