package com.example.junyoung.culivebus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;

import com.example.junyoung.culivebus.fragment.LocationPermissionFragment;

import java.util.UUID;

public class PermissionActivity extends FragmentActivity {
  public static final String EXTRA_PERMISSION_GRANTED
    = "com.example.junyoung.uiucbus.EXTRA_PERMISSION_GRANTED";

  private String mUid;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_permission);

    createUid();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      Window w = getWindow();
      w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    if (findViewById(R.id.fragment_container_activity_permission) != null) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      LocationPermissionFragment locationPermissionFragment = new LocationPermissionFragment();
      transaction.add(R.id.fragment_container_activity_permission, locationPermissionFragment);
      transaction.commit();
    }
  }

  private void createUid() {
    mUid = UUID.randomUUID().toString();
    SharedPreferences sharedPref = this.getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString(getString(R.string.saved_uid), mUid);
    editor.apply();
  }
}
