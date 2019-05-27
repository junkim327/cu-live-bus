package com.example.junyoung.culivebus.util.pref;

import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

public class BooleanPreference {
  private final SharedPreferences preferences;
  private final String name;
  private final Boolean defaultValue;

  public BooleanPreference(@NotNull SharedPreferences sharedPreferences, @NotNull String name,
                           boolean defaultValue) {
    this.preferences = sharedPreferences;
    this.name = name;
    this.defaultValue = defaultValue;
  }

  public Boolean getValue() {
    return preferences.getBoolean(name, defaultValue);
  }

  public void setValue(boolean value) {
    preferences.edit().putBoolean(name, value).apply();
  }
}
