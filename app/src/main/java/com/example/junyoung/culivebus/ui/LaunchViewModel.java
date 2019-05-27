package com.example.junyoung.culivebus.ui;

import com.example.junyoung.culivebus.usecase.PermissionCompletedUseCase;
import com.example.junyoung.culivebus.util.Event;
import com.example.junyoung.culivebus.usecase.OnboardingCompletedUseCase;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.vo.Status;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class LaunchViewModel extends ViewModel {
  private final MutableLiveData<Resource<Boolean>> onboardingCompletedResult =
    new MutableLiveData<>();
  private final LiveData<Event<LaunchDestination>> launchDestination;

  @Inject
  LaunchViewModel(OnboardingCompletedUseCase onboardingCompletedUseCase,
                  PermissionCompletedUseCase permissionCompletedUseCase) {
    onboardingCompletedUseCase.invoke(onboardingCompletedResult);
    launchDestination = Transformations.map(onboardingCompletedResult, input -> {
      if (input.status == Status.SUCCESS && !input.data) {
        Timber.d("Open Permission Activity");
        return new Event<>(LaunchDestination.PERMISSION);
      } else {
        Timber.d("Open Dashboard Activity");
        return new Event<>(LaunchDestination.MAIN);
      }
    });
  }

  public LiveData<Event<LaunchDestination>> getLaunchDestination() {
    return launchDestination;
  }

  public enum LaunchDestination {
    PERMISSION,
    MAIN
  }
}
