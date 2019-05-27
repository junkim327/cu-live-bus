package com.example.junyoung.culivebus.ui.common;

import com.example.junyoung.culivebus.DirectionActivity;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.ui.direction.result.DirectionResultFragment;
import com.example.junyoung.culivebus.ui.direction.search.DirectionSearchFragment;
import com.example.junyoung.culivebus.ui.direction.search.SearchHistoryFragment;

import javax.inject.Inject;

import androidx.fragment.app.FragmentManager;

public class DirectionNavigationController {
  private final int containerId;
  private final FragmentManager fragmentManager;

  @Inject
  public DirectionNavigationController(DirectionActivity directionActivity) {
    this.containerId = R.id.framelayout_direction;
    this.fragmentManager = directionActivity.getSupportFragmentManager();
  }

  public void navigateToResult(boolean shouldAddToBackStack) {
    DirectionResultFragment fragment = new DirectionResultFragment();
    if (shouldAddToBackStack) {
      fragmentManager.beginTransaction()
        .replace(containerId, fragment)
        .commitAllowingStateLoss();
    } else {
      fragmentManager.beginTransaction()
        .replace(containerId, fragment)
        .commitAllowingStateLoss();
    }
  }

  public void navigateToSearch() {
    DirectionSearchFragment fragment = new DirectionSearchFragment();
    fragmentManager.beginTransaction()
      .replace(containerId, fragment)
      .addToBackStack(null)
      .commitAllowingStateLoss();
  }

  public void navigateToSearchHistory() {
    SearchHistoryFragment fragment = new SearchHistoryFragment();
    fragmentManager.beginTransaction()
      .replace(containerId, fragment)
      .addToBackStack(null)
      .commitAllowingStateLoss();
  }
}
