package com.example.junyoung.uiucbus.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.uiucbus.MainTempActivity;
import com.example.junyoung.uiucbus.R;

import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LocationPermissionFragment extends Fragment {
  public static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 99;
  public static final String COMPLETED_LOCATION_PERMISSION
    = "com.example.junyoung.uiucbus.fragments.COMPLETED_LOCATION_PERMISSION";

  private String mUid;

  private Unbinder mUnbinder;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences sharedPref = getContext().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );

    mUid = sharedPref.getString(getString(R.string.saved_uid), null);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_location_permission, container, false);
    mUnbinder = ButterKnife.bind(this, view);

    return view;
  }

  @OnClick(R.id.button_ok_activity_permission)
  public void requestLocationPermission() {
    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
      != PackageManager.PERMISSION_GRANTED) {
      this.requestPermissions(
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
        MY_PERMISSIONS_ACCESS_FINE_LOCATION
      );
    } else {
      startMainActivity();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    Log.d("LocationPermissionFrag", String.valueOf(requestCode));
    switch (requestCode) {
      case MY_PERMISSIONS_ACCESS_FINE_LOCATION:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // Permission was granted.
          Log.d("LocationPermissionFrag", "Permission was granted");
          saveLocationPermissionToSharedPref();
          startMainActivity();
        } else {
          // Permission denied, open new fragment
        }
    }
  }

  private void saveLocationPermissionToSharedPref() {
    SharedPreferences sharedPref = getContext().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putBoolean(getString(R.string.saved_user_location_permission), true);
    editor.apply();
  }

  private void createUid() {
    mUid = UUID.randomUUID().toString();
    SharedPreferences sharedPref = getContext().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString(getString(R.string.saved_uid), mUid);
    editor.apply();
  }

  private void startMainActivity() {
    if (mUid == null) {
      createUid();
    }

    if (getActivity() != null) {
      Intent intent = new Intent(getActivity(), MainTempActivity.class);
      getActivity().startActivity(intent);
      getActivity().finish();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }
}
