package com.example.junyoung.culivebus.ui.common;

import com.example.junyoung.culivebus.MainActivity;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.example.junyoung.culivebus.fragment.DashboardFragment;
import com.example.junyoung.culivebus.fragment.DepartureFragment;
import com.example.junyoung.culivebus.ui.download.DownloadFragment;
import com.example.junyoung.culivebus.ui.search.SearchFragment;

import javax.inject.Inject;

import androidx.fragment.app.FragmentManager;

public class NavigationController {
  private final int containerId;
  private final FragmentManager fragmentManager;

  @Inject
  public NavigationController(MainActivity mainActivity) {
    this.containerId = R.id.framelayout_main;
    this.fragmentManager = mainActivity.getSupportFragmentManager();
  }

  public void navigateToDownload() {
    DownloadFragment downloadFragment = new DownloadFragment();
    fragmentManager.beginTransaction()
      .replace(containerId, downloadFragment)
      .commitAllowingStateLoss();
  }

  public void navigateToDashboard() {
    DashboardFragment dashboardFragment = new DashboardFragment();
    fragmentManager.beginTransaction()
      .replace(containerId, dashboardFragment)
      .commitAllowingStateLoss();
  }

  public void navigateToSearch() {
    SearchFragment searchFragment = new SearchFragment();
    fragmentManager.beginTransaction()
      .replace(containerId, searchFragment)
      .addToBackStack(null)
      .commitAllowingStateLoss();
  }

  public void navigateToDeparture(StopPoint stopPoint) {
    DepartureFragment departureFragment = DepartureFragment.create(stopPoint);
    fragmentManager.beginTransaction()
      .replace(containerId, departureFragment)
      .addToBackStack(null)
      .commitAllowingStateLoss();
  }
}
