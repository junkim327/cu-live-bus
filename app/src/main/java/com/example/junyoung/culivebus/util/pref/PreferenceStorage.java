package com.example.junyoung.culivebus.util.pref;

public interface PreferenceStorage {
  boolean getOnboardingCompleted();

  void setOnboardingCompleted(boolean value);

  boolean getPermissionCompleted();

  void setPermissionCompleted(boolean value);
}
