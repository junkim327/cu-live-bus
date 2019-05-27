package com.example.junyoung.culivebus.ui.settings;

import android.os.Bundle;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.ui.PreferenceFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import dagger.android.support.DaggerAppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements
  PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    getSupportFragmentManager().beginTransaction()
      .add(R.id.framelayout_settings, new PreferenceFragment())
      .commit();
  }

  @Override
  public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
    final Bundle args = pref.getExtras();
    final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
      getClassLoader(),
      pref.getFragment(),
      args
    );
    fragment.setArguments(args);
    fragment.setTargetFragment(caller, 0);
    getSupportFragmentManager().beginTransaction()
      .replace(R.id.framelayout_settings, fragment)
      .addToBackStack(null)
      .commit();

    return true;
  }
}
