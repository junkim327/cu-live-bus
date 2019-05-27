package com.example.junyoung.culivebus.util.pref;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class SharedPreferenceStorage implements PreferenceStorage {
  private static final String PREFS_NAME = "cu_live_bus";
  private static final String PREFS_ONBOARDING = "pref_onboarding";
  private static final String PREFS_PERMISSION = "pref_permission";

  private final SharedPreferences prefs;
  private final BooleanPreference onboardingCompleted;
  private final BooleanPreference permissionCompleted;

  @Inject
  public SharedPreferenceStorage(Context context) {
    prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    onboardingCompleted = new BooleanPreference(prefs, PREFS_ONBOARDING, false);
    permissionCompleted = new BooleanPreference(prefs, PREFS_PERMISSION, false);
  }

  @Override
  public boolean getOnboardingCompleted() {
    return onboardingCompleted.getValue();
  }

  @Override
  public void setOnboardingCompleted(boolean value) {
    onboardingCompleted.setValue(value);
  }

  @Override
  public boolean getPermissionCompleted() {
    return permissionCompleted.getValue();
  }

  @Override
  public void setPermissionCompleted(boolean value) {
    permissionCompleted.setValue(value);
  }
}
