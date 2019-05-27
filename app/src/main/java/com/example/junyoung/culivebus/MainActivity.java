package com.example.junyoung.culivebus;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.junyoung.culivebus.ui.MainNavigationFragment;
import com.example.junyoung.culivebus.ui.common.NavigationController;
import com.example.junyoung.culivebus.ui.dashboard.DashboardFragment;
import com.example.junyoung.culivebus.ui.nearbystop.NearByStopFragment;
import com.example.junyoung.culivebus.ui.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

public class MainActivity extends DaggerAppCompatActivity {
  @Inject
  DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
  @Inject
  NavigationController navigationController;

  private MainNavigationFragment currentFragment;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    BottomNavigationView navigation = findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Timber.d("Selected ID : %s", item.getItemId());
        boolean selected;
        switch (item.getItemId()) {
          case R.id.navigation_dashboard:
            navigationController.navigateToDashboard();
            selected = true;
            break;
          case R.id.navigation_search:
            navigationController.navigateToSearch();
            selected = true;
            break;
          case R.id.navigation_map:
            navigationController.navigateToMap();
            selected = true;
            break;
          case R.id.navigation_direction:
            navigationController.navigateToDirection();
            selected = true;
            break;
          default:
            selected = false;
        }

        return selected;
      }
    });

    if (savedInstanceState == null) {
      Timber.d("savedInstanceState == null");
      navigation.setSelectedItemId(R.id.navigation_dashboard);
    }

    navigation.setOnNavigationItemReselectedListener(item -> {
      Timber.d("Reselected ID : %s", item.getItemId());
    });
  }

  @Override
  public void onBackPressed() {
    if (!currentFragment.onBackPressed()) {
      super.onBackPressed();
    }
  }
}
