package com.example.junyoung.culivebus.ui.download;

import com.example.junyoung.culivebus.repository.BusStopRepository;
import com.example.junyoung.culivebus.usecase.OnboardingCompletedUseCase;
import com.example.junyoung.culivebus.util.AbsentLiveData;
import com.example.junyoung.culivebus.vo.Resource;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class DownloadViewModel extends ViewModel {
  private final OnboardingCompletedUseCase onboardingCompletedUseCase;
  private final LiveData<Resource<Integer>> numStops;
  private final MutableLiveData<String> apiKey = new MutableLiveData<>();

  @Inject
  DownloadViewModel(OnboardingCompletedUseCase onboardingCompletedUseCase,
                    BusStopRepository busStopRepository) {
    this.onboardingCompletedUseCase = onboardingCompletedUseCase;
    numStops = Transformations.switchMap(apiKey, input -> {
      if (input == null) {
        return AbsentLiveData.create();
      } else {
        return busStopRepository.insertBusStops(input);
      }
    });
  }

  public void refresh() {
    if (apiKey.getValue() != null) {
      apiKey.setValue(apiKey.getValue());
    }
  }

  public LiveData<Resource<Integer>> getNumStops() {
    return numStops;
  }


  public void setApiKey(String apiKey) {
    this.apiKey.setValue(apiKey);
  }

  public void saveOnboardingCompletedResult(boolean onboardingCompleted) {
    onboardingCompletedUseCase.saveResult(onboardingCompleted);
  }
}
