package com.example.junyoung.culivebus.ui.permission;

import android.os.Bundle;

import com.example.junyoung.culivebus.R;


import androidx.annotation.Nullable;
import dagger.android.support.DaggerAppCompatActivity;

public class PermissionActivity extends DaggerAppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_permission);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
        .add(R.id.framelayout_permission, new PermissionFragment())
        .commit();
    }
  }
}
