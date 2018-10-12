package com.example.junyoung.uiucbus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

public class MainEmptyActivity extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent activityIntent;

    SharedPreferences sharedPref = this.getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    if (sharedPref.getBoolean(
      getString(R.string.saved_user_location_permission), false)) {
      activityIntent = new Intent(this, PermissionActivity.class);
    } else {
      String uid = sharedPref.getString(getString(R.string.saved_uid), null);
      if (uid == null) {
        createUid();
      }
      activityIntent = new Intent(this, MainActivity.class);
    }

    startActivity(activityIntent);
    finish();
  }

  private void createUid() {
    String uid = UUID.randomUUID().toString();
    SharedPreferences sharedPref = this.getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString(getString(R.string.saved_uid), uid);
    editor.apply();
  }
}
