package com.example.junyoung.culivebus.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.junyoung.culivebus.MainActivity;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
  @Rule
  public ActivityTestRule<MainActivity> mActivityRule =
    new ActivityTestRule<>(MainActivity.class);
}
