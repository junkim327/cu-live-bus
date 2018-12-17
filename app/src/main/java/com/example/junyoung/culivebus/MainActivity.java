package com.example.junyoung.culivebus;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.example.junyoung.culivebus.fragment.DepartureFragment;
import com.example.junyoung.culivebus.ui.route.RouteFragment;
import com.example.junyoung.culivebus.ui.common.NavigationController;
import com.example.junyoung.culivebus.ui.search.SearchFragment;
import com.example.junyoung.culivebus.fragment.DashboardFragment;
import com.example.junyoung.culivebus.fragment.NoInternetConnectionFragment;
import com.example.junyoung.culivebus.fragment.SettingFragment;
import com.example.junyoung.culivebus.util.listener.OnHomeItemClickedListener;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector,
  OnInternetConnectedListener,
  OnHomeItemClickedListener,
  RouteFragment.OnBusStopClickedListener,
  DashboardFragment.OnSettingItemSelectedListener,
  DepartureFragment.OnDepartureClickedListener {
  private static final String TAG = MainActivity.class.getSimpleName();

  private SettingFragment mSettingFragment;
  private RouteFragment mRouteFragment;
  private DashboardFragment mDashboardFragment;
  private SearchFragment mSearchFragment;
  private DepartureFragment mDepartureFragment;

  @Inject
  DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
  @Inject
  NavigationController navigationController;

  @BindView(R.id.framelayout_main)
  FrameLayout mContainer;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    if (savedInstanceState == null) {
      navigationController.navigateToDashboard();
    }
  }

  private void replaceFragment(Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
      .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
      .replace(mContainer.getId(), fragment)
      .addToBackStack(null)
      .commit();
  }

  private void performFragmentOperation(Fragment fragmentInstance) {
    if (mContainer != null) {
      int mainFrameLayoutId = mContainer.getId();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      if (getSupportFragmentManager().findFragmentById(mainFrameLayoutId) != null) {
        transaction.replace(mainFrameLayoutId, fragmentInstance);
      } else {
        transaction.add(mainFrameLayoutId, fragmentInstance);
      }
      transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onInternetConnected(boolean isConnected, boolean shouldHideFragment) {
    if (!isConnected) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      if (mContainer != null) {
        NoInternetConnectionFragment noInternetConnectionFragment =
          new NoInternetConnectionFragment();
        if (getSupportFragmentManager().findFragmentById(mContainer.getId()) != null) {
          transaction.replace(mContainer.getId(), noInternetConnectionFragment);
        } else {
          transaction.add(mContainer.getId(), noInternetConnectionFragment);
        }
      }
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onSettingItemSelected() {
    if (mContainer != null) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
        R.anim.enter_from_left, R.anim.exit_to_right);
      mSettingFragment = new SettingFragment();
      if (getSupportFragmentManager().findFragmentById(mContainer.getId()) != null) {
        transaction.replace(mContainer.getId(), mSettingFragment);
      } else {
        transaction.add(mContainer.getId(), mSettingFragment);
      }
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onBusStopClicked() {
    if (mDepartureFragment == null) {
      mDepartureFragment = new DepartureFragment();
    }

    replaceFragment(mDepartureFragment);
  }

  @Override
  public void onHomeItemClicked() {
    performFragmentOperation(mDashboardFragment);
  }

  @Override
  public void onDepartureClicked() {
    mRouteFragment = new RouteFragment();
    performFragmentOperation(mRouteFragment);
  }

  @Override
  public AndroidInjector<Fragment> supportFragmentInjector() {
    return dispatchingAndroidInjector;
  }
}
