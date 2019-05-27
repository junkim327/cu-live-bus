package com.example.junyoung.culivebus.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.junyoung.culivebus.MainActivity;
import com.example.junyoung.culivebus.ui.permission.PermissionActivity;
import com.example.junyoung.culivebus.util.EventObserver;
import com.example.junyoung.culivebus.vo.Function;
import com.example.junyoung.culivebus.ui.LaunchViewModel.LaunchDestination;


import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

public class LauncherActivity extends DaggerAppCompatActivity {
  @Inject
  ViewModelProvider.Factory viewModelFactory;

  private LaunchViewModel launchViewModel;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    launchViewModel = ViewModelProviders.of(this, viewModelFactory)
      .get(LaunchViewModel.class);

    launchViewModel.getLaunchDestination().observe(this, new EventObserver<>(
      new Function<LaunchDestination>() {
        @Override
        public void invoke(LaunchDestination content) {
          Timber.d("Content : %s", content);
          Intent intent;

          switch (content) {
            case PERMISSION:
              intent = new Intent(LauncherActivity.this, PermissionActivity.class);
              break;
            default:
              intent = new Intent(LauncherActivity.this, MainActivity.class);
              break;
          }
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
          startActivity(intent);
        }
      })
    );
  }
}
