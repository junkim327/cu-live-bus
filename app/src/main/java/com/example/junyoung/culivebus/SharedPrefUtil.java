package com.example.junyoung.culivebus;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefUtil {
  public static ArrayList<String> getArrayList(SharedPreferences sharedPref, String key) {
    Gson gson = new Gson();
    String json = sharedPref.getString(key, null);
    if (json == null) {
      return new ArrayList<>();
    }
    Type type = new TypeToken<ArrayList<String>>() {}.getType();

    return gson.fromJson(json, type);
  }
}
