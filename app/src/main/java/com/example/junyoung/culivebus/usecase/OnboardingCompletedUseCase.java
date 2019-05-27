package com.example.junyoung.culivebus.usecase;

import com.example.junyoung.culivebus.util.AppExecutors;
import com.example.junyoung.culivebus.util.pref.PreferenceStorage;
import com.example.junyoung.culivebus.vo.Resource;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;


public class OnboardingCompletedUseCase {
  private final AppExecutors appExecutors;
  private final PreferenceStorage preferenceStorage;

  @Inject
  OnboardingCompletedUseCase(AppExecutors appExecutors, PreferenceStorage preferenceStorage) {
    this.appExecutors = appExecutors;
    this.preferenceStorage = preferenceStorage;
  }

  public void invoke(MutableLiveData<Resource<Boolean>> result) {
    try {
      appExecutors.diskIO().execute(() -> {
        try {
          result.postValue(Resource.success(preferenceStorage.getOnboardingCompleted()));
        } catch (Exception e) {
          Timber.e(e);
          if (e.getMessage() != null) {
            result.postValue(Resource.error(e.getMessage(), null));
          }
        }
      });
    } catch (Exception e) {
      Timber.d(e);
      if (e.getMessage() != null) {
        result.postValue(Resource.error(e.getMessage(), null));
      }
    }
  }

  public void saveResult(boolean onBoardingCompleted) {
    try {
      appExecutors.diskIO().execute(() -> {
        try {
          preferenceStorage.setOnboardingCompleted(onBoardingCompleted);
        } catch (Exception e) {
          Timber.e(e);
        }
      });
    } catch (Exception e) {
      Timber.d(e);
    }
  }
}
