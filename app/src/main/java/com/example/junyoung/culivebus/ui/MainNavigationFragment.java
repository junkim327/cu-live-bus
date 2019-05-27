package com.example.junyoung.culivebus.ui;

public interface MainNavigationFragment {
  default Boolean onBackPressed() {
    return false;
  }

  default void onUserInteraction() {}
}
