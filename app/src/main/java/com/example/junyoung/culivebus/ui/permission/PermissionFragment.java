package com.example.junyoung.culivebus.ui.permission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.binding.FragmentDataBindingComponent;
import com.example.junyoung.culivebus.databinding.PermissionFragmentBinding;
import com.example.junyoung.culivebus.ui.download.DownloadActivity;
import com.example.junyoung.culivebus.util.AutoClearedValue;
import com.example.junyoung.culivebus.util.EventObserver;
import com.example.junyoung.culivebus.vo.Function;


import javax.inject.Inject;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerFragment;
import timber.log.Timber;

public class PermissionFragment extends DaggerFragment {
  public static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 99;
  public static final String COMPLETED_LOCATION_PERMISSION
    = "com.example.junyoung.uiucbus.fragments.COMPLETED_LOCATION_PERMISSION";

  @Inject
  ViewModelProvider.Factory viewModelFactory;

  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<PermissionFragmentBinding> binding;

  private PermissionViewModel permissionViewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    permissionViewModel = ViewModelProviders.of(this, viewModelFactory)
      .get(PermissionViewModel.class);
    PermissionFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.fragment_permission, container, false, dataBindingComponent);
    dataBinding.setLifecycleOwner(this);
    dataBinding.setViewModel(permissionViewModel);
    binding = new AutoClearedValue<>(this, dataBinding);

    return dataBinding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    permissionViewModel.getClickPermissionButtonAction().observe(getViewLifecycleOwner(),
      new EventObserver<>(new Function<Boolean>() {
      @Override
      public void invoke(Boolean content) {
        Timber.d("Invoked");
        requestLocationPermission();
      }
    }));
  }


  private void requestLocationPermission() {
    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
      != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(
        new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
        MY_PERMISSIONS_ACCESS_FINE_LOCATION
      );
    } else {
      startActivity(new Intent(getActivity(), DownloadActivity.class));
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode == MY_PERMISSIONS_ACCESS_FINE_LOCATION) {
      Timber.d("Called");
      startActivity(new Intent(getActivity(), DownloadActivity.class));
    }
    /*switch (requestCode) {
      case MY_PERMISSIONS_ACCESS_FINE_LOCATION:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // Permission was granted.

          //navigationController.navigateToDownload();
        } else {
          // Permission denied, open new fragment

        }
        startActivity(new Intent(getActivity(), DownloadActivity.class));
    }*/
  }

  private void saveLocationPermissionToSharedPref(boolean isPermissionGranted) {
    SharedPreferences sharedPref = this.getActivity().getSharedPreferences(
      getString(R.string.preference_file_key),
      Context.MODE_PRIVATE
    );
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putBoolean(getString(R.string.saved_user_location_permission), isPermissionGranted);
    editor.apply();
  }
}
