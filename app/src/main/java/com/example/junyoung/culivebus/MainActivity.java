package com.example.junyoung.culivebus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.example.junyoung.culivebus.fragment.BusDeparturesFragment;
import com.example.junyoung.culivebus.fragment.BusRouteFragment;
import com.example.junyoung.culivebus.ui.stopsearch.BusStopSearchFragment;
import com.example.junyoung.culivebus.fragment.DashboardFragment;
import com.example.junyoung.culivebus.fragment.NoInternetConnectionFragment;
import com.example.junyoung.culivebus.fragment.SettingFragment;
import com.example.junyoung.culivebus.util.listener.OnHomeItemClickedListener;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnInternetConnectedListener,
  OnHomeItemClickedListener,
  DashboardFragment.OnSettingItemSelectedListener,
  DashboardFragment.OnBusStopsSearchviewClickedListener,
  BusStopSearchFragment.OnBusStopSelectedListener,
  BusDeparturesFragment.OnDepartureClickedListener {
  private static final String TAG = MainActivity.class.getSimpleName();

  private SettingFragment mSettingFragment;
  private BusRouteFragment mBusRouteFragment;
  private DashboardFragment mDashboardFragment;
  private BusStopSearchFragment mBusStopSearchFragment;

  @BindView(R.id.framelayout_main)
  FrameLayout mMainFrameLayout;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_temp);
    ButterKnife.bind(this);

    if (savedInstanceState == null) {
      mDashboardFragment = new DashboardFragment();
    }

    if (mMainFrameLayout != null) {
      getSupportFragmentManager().beginTransaction()
        .add(mMainFrameLayout.getId(), mDashboardFragment)
        .commit();
    }
  }

  private void performFragmentOperation(Fragment fragmentInstance) {
    if (mMainFrameLayout != null) {
      int mainFrameLayoutId = mMainFrameLayout.getId();
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
      if (mMainFrameLayout != null) {
        NoInternetConnectionFragment noInternetConnectionFragment =
          new NoInternetConnectionFragment();
        if (getSupportFragmentManager().findFragmentById(mMainFrameLayout.getId()) != null) {
          transaction.replace(mMainFrameLayout.getId(), noInternetConnectionFragment);
        } else {
          transaction.add(mMainFrameLayout.getId(), noInternetConnectionFragment);
        }
      }
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onSettingItemSelected() {
    if (mMainFrameLayout != null) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
        R.anim.enter_from_left, R.anim.exit_to_right);
      mSettingFragment = new SettingFragment();
      if (getSupportFragmentManager().findFragmentById(mMainFrameLayout.getId()) != null) {
        transaction.replace(mMainFrameLayout.getId(), mSettingFragment);
      } else {
        transaction.add(mMainFrameLayout.getId(), mSettingFragment);
      }
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onBusStopsSearchviewClicked() {
    if (mMainFrameLayout != null) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      mBusStopSearchFragment = new BusStopSearchFragment();
      if (getSupportFragmentManager().findFragmentById(mMainFrameLayout.getId()) != null) {
        transaction.replace(mMainFrameLayout.getId(), mBusStopSearchFragment);
      } else {
        transaction.add(mMainFrameLayout.getId(), mBusStopSearchFragment);
      }
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onBusStopSelected() {
    if (mMainFrameLayout != null) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      BusDeparturesFragment frag = new BusDeparturesFragment();
      if (getSupportFragmentManager().findFragmentById(mMainFrameLayout.getId()) != null) {
        transaction.replace(mMainFrameLayout.getId(), frag);
      } else {
        transaction.add(mMainFrameLayout.getId(), frag);
      }
      transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onHomeItemClicked() {
    performFragmentOperation(mDashboardFragment);
  }

  @Override
  public void onDepartureClicked() {
    mBusRouteFragment = new BusRouteFragment();
    performFragmentOperation(mBusRouteFragment);
  }
}
