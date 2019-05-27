package com.example.junyoung.culivebus.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;

import androidx.preference.PreferenceFragmentCompat;
import timber.log.Timber;

public class PreferenceFragment extends PreferenceFragmentCompat {
  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    Timber.d("onCreatePreferences");
    setPreferencesFromResource(R.xml.preferences, rootKey);
  }
}
