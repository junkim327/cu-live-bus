package com.example.junyoung.culivebus.ui.permission;

import com.example.junyoung.culivebus.usecase.PermissionCompletedUseCase;
import com.example.junyoung.culivebus.util.Event;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.vo.Status;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class PermissionViewModel extends ViewModel {
  private final MutableLiveData<Event<Boolean>> clickPermissionButtonAction =
    new MutableLiveData<>();

  @Inject
  PermissionViewModel() {
  }

  public void permissionButtonClicked() {
    Timber.d("Clicked");
    clickPermissionButtonAction.setValue(new Event<>(true));
  }

  public LiveData<Event<Boolean>> getClickPermissionButtonAction() {
    return clickPermissionButtonAction;
  }
}
