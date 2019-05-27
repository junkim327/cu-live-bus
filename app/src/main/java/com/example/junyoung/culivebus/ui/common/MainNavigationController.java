package com.example.junyoung.culivebus.ui.common;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.ui.PreferenceFragment;
import com.example.junyoung.culivebus.ui.dashboard.DashboardFragment;
import com.example.junyoung.culivebus.ui.departure.DepartureActivity;
import com.example.junyoung.culivebus.ui.departure.DepartureFragment;
import com.example.junyoung.culivebus.ui.download.DownloadFragment;
import com.example.junyoung.culivebus.ui.nearbystop.NearByStopFragment;
import com.example.junyoung.culivebus.ui.route.RouteFragment;
import com.example.junyoung.culivebus.ui.search.SearchFragment;
import com.example.junyoung.culivebus.ui.settings.ThirdPartySoftwareFragment;

import javax.inject.Inject;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainNavigationController {
  private final int containerId;
  private final FragmentManager fragmentManager;

  @Inject
  public MainNavigationController(DepartureActivity activity) {
    this.containerId = 1;
    this.fragmentManager = activity.getSupportFragmentManager();
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
      .commit();
  }

  public void navigateToSearch() {
    SearchFragment searchFragment = new SearchFragment();
    fragmentManager.beginTransaction()
      .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
      .replace(containerId, searchFragment)
      .addToBackStack(null)
      .commitAllowingStateLoss();
  }

  public void navigateToDeparture() {
    DepartureFragment departureFragment = new DepartureFragment();
    fragmentManager.beginTransaction()
      .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
      .replace(containerId, departureFragment)
      .addToBackStack(null)
      .commitAllowingStateLoss();
  }

  public void navigateToNearByStop() {
    NearByStopFragment nearByStopFragment = new NearByStopFragment();
    fragmentManager.beginTransaction()
      .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
      .replace(containerId, nearByStopFragment)
      .addToBackStack(null)
      .commitAllowingStateLoss();
  }

  public void navigateToRoute() {
    RouteFragment routeFragment = new RouteFragment();
    fragmentManager.beginTransaction()
      .replace(containerId, routeFragment)
      .addToBackStack(null)
      .commitAllowingStateLoss();
  }

  public void navigateToThirdPartySoftware() {
    ThirdPartySoftwareFragment fragment = new ThirdPartySoftwareFragment();
    fragmentManager.beginTransaction()
      .replace(containerId, fragment)
      .commitAllowingStateLoss();
  }

  public void navigateToSettings() {
    PreferenceFragment fragment = new PreferenceFragment();
    fragmentManager.beginTransaction()
      .replace(containerId, fragment)
      .commitAllowingStateLoss();
  }
}
