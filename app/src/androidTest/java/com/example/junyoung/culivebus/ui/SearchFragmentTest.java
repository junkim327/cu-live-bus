package com.example.junyoung.culivebus.ui;

import android.content.Context;

import com.example.junyoung.culivebus.testing.SingleFragmentActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
public class SearchFragmentTest {
  @Rule
  public ActivityTestRule<SingleFragmentActivity> activityRule =
    new ActivityTestRule<>(SingleFragmentActivity.class, true, true);

  @Before
  public void init() {
    Context context = ApplicationProvider.getApplicationContext();
  }
}
