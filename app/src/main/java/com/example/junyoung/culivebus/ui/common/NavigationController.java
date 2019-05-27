package com.example.junyoung.culivebus.ui.common;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.example.junyoung.culivebus.httpclient.pojos.Departure;
import com.example.junyoung.culivebus.ui.dashboard.DashboardFragment;
import com.example.junyoung.culivebus.ui.departure.DepartureActivity;
import com.example.junyoung.culivebus.ui.direction.result.DirectionResultFragment;
import com.example.junyoung.culivebus.ui.direction.search.DirectionSearchFragment;
import com.example.junyoung.culivebus.ui.direction.search.SearchHistoryFragment;
import com.example.junyoung.culivebus.ui.nearbystop.NearByStopFragment;
import com.example.junyoung.culivebus.ui.route.RouteActivity;
import com.example.junyoung.culivebus.ui.route.RouteFragment;
import com.example.junyoung.culivebus.ui.search.SearchFragment;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NavigationController {
  private final int containerId;
  private final AppCompatActivity activity;
  private final FragmentManager fragmentManager;

  @Inject
  NavigationController(AppCompatActivity activity) {
    this.containerId = R.id.fragment_container;
    this.activity = activity;
    this.fragmentManager = activity.getSupportFragmentManager();
  }

  public void navigateToDashboard() {
    replaceFragment(new DashboardFragment());
  }

  public void navigateToSearch() {
    replaceFragment(new SearchFragment());
  }

  public void navigateToMap() {
    replaceFragment(new NearByStopFragment());
  }

  public void navigateToDirection() {
    replaceFragment(new DirectionResultFragment());
  }

  public void navigateToRoute(String tripId, String shapeId, String vehicleId,
                              String headSign, String routeColor) {
    replaceFragment(RouteFragment.newInstance(tripId, shapeId, vehicleId, headSign, routeColor));
  }

  public void navigateToDirectionSearch() {
    replaceFragment(new DirectionSearchFragment());
  }

  public void navigateToSearchHistory() {
    replaceFragment(new SearchHistoryFragment());
  }

  public void replaceFragment(Fragment fragment) {
    FragmentTransaction transaction = fragmentManager
      .beginTransaction()
      .replace(containerId, fragment);

    if (fragmentManager.isStateSaved()) {
      transaction.commitAllowingStateLoss();
    } else {
      transaction.commit();
    }
  }

  public void navigateToDepartureActivity(StopPoint stopPoint) {
    activity.startActivity(DepartureActivity.starterIntent(activity, stopPoint));
  }

  public void navigateToRouteActivity(Departure departure) {
    activity.startActivity(RouteActivity.starterIntent(activity, departure));
  }
}
