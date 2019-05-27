package com.example.junyoung.culivebus.binding;

import android.view.View;

import androidx.databinding.BindingAdapter;
import timber.log.Timber;

public class BindingAdapters {
  @BindingAdapter("visibleGone")
  public static void showHide(View view, boolean shouldHide) {
    view.setVisibility(shouldHide ? View.GONE : View.VISIBLE);
  }

  @BindingAdapter("shouldMakeViewInvisible")
  public static void shouldMakeViewInvisible(View view, boolean shouldMakeInvisible) {
    Timber.d("Should make invisible %s", shouldMakeInvisible);
    view.setVisibility(shouldMakeInvisible ? View.INVISIBLE : View.VISIBLE);
  }
}
