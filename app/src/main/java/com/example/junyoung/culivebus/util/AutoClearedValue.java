package com.example.junyoung.culivebus.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class AutoClearedValue<T> {
  private T value;
  public AutoClearedValue(Fragment fragment, T value) {
    FragmentManager fragmentManager = fragment.getFragmentManager();
    fragmentManager.registerFragmentLifecycleCallbacks(
      new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentViewDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
          if (f == fragment) {
            AutoClearedValue.this.value = null;
            fragmentManager.unregisterFragmentLifecycleCallbacks(this);
          }
        }
      }, false);
    this.value = value;
  }

  public T get() {
    return value;
  }
}
