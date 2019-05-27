package com.example.junyoung.culivebus.ui.dashboard;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.databinding.HomeFragmentBinding;
import com.example.junyoung.culivebus.db.entity.FavoriteStop;
import com.example.junyoung.culivebus.ui.MainNavigationFragment;
import com.example.junyoung.culivebus.ui.settings.SettingsActivity;
import com.example.junyoung.culivebus.util.AutoClearedValue;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.adapter.BusFavoriteDeparturesAdapter;
import com.example.junyoung.culivebus.vo.Resource;
import com.example.junyoung.culivebus.vo.SortedFavoriteDeparture;
import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SpringSystem;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class DashboardFragment extends DaggerFragment implements MainNavigationFragment {
  @Inject
  ViewModelProvider.Factory viewModelFactory;

  AutoClearedValue<HomeFragmentBinding> binding;

  private DashboardListAdapter adapter;

  private DashboardViewModel dashboardViewModel;
  private final BaseSpringSystem mSpringSystem = SpringSystem.create();
  private final LatLng defaultLocation = new LatLng(40.109659, -88.227159);

  private BusFavoriteDeparturesAdapter mAdapter;
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Timber.d("onCreate has called.");

    setHasOptionsMenu(true);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    dashboardViewModel = ViewModelProviders.of(this, viewModelFactory)
      .get(DashboardViewModel.class);
    HomeFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.fragment_dashboard, container, false);

    dataBinding.setViewModel(dashboardViewModel);
    dataBinding.setLifecycleOwner(getViewLifecycleOwner());

    adapter = new DashboardListAdapter(getViewLifecycleOwner(), dashboardViewModel);
    dataBinding.favoriteDepartureList.setAdapter(adapter);

    binding = new AutoClearedValue<>(this, dataBinding);

    return dataBinding.getRoot();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    initToolbar();

    dashboardViewModel.getFavoriteStops().observe(getViewLifecycleOwner(),
      new Observer<List<FavoriteStop>>() {
      @Override
      public void onChanged(List<FavoriteStop> favoriteStops) {
        if (favoriteStops != null && favoriteStops.size() > 0) {
          dashboardViewModel.getFavoriteStopsDepartures(favoriteStops);
        }
      }
    });

    dashboardViewModel.getFavoriteDepartures().observe(getViewLifecycleOwner(),
      new Observer<Resource<List<SortedFavoriteDeparture>>>() {
      @Override
      public void onChanged(Resource<List<SortedFavoriteDeparture>> resource) {
        if (resource != null) {
          processFavoriteDepartureResource(resource);
          Timber.d("Status : %s", resource.status);
        }
      }
    });
  }

  private void processFavoriteDepartureResource(Resource<List<SortedFavoriteDeparture>> resource) {
    switch (resource.status) {
      case LOADING:
        break;
      case SUCCESS:
        adapter.updateFavoriteDepartures(resource.data);
        break;
    }
  }

//  private void setRecyclerView() {
//    mRecyclerView.setHasFixedSize(true);
//    mRecyclerView.setNestedScrollingEnabled(false);
//
//    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//    mRecyclerView.setLayoutManager(layoutManager);
//    ((SimpleItemAnimator) mRecyclerView.getItemAnimator())
//      .setSupportsChangeAnimations(false);
//
//    RecyclerviewClickListener departureClickListener = new RecyclerviewClickListener() {
//      @Override
//      public void onClick(View view, int position) {
//
//      }
//    };
//    mAdapter = new BusFavoriteDeparturesAdapter(getContext(), departureClickListener);
//    mRecyclerView.setAdapter(mAdapter);
//  }

//  private void processResponse(Resource<List<FavoriteBusDepartures>> resource) {
//    switch (resource.status) {
//      case LOADING:
//        mProgressBar.setVisibility(VISIBLE);
//        break;
//
//      case SUCCESS:
//        mProgressBar.setVisibility(GONE);
//        if (resource.data != null) {
//          mAdapter.updateBusDepartures(resource.data);
//        }
//        break;
//
//      case ERROR:
//        mProgressBar.setVisibility(GONE);
//        break;
//    }
//  }

  @Override
  public void onStart() {
    super.onStart();

//    if (mUid != null) {
//      mDisposable.add(mViewModel.loadAllBusStopsByUid(mUid)
//        .subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(userSavedBusStops -> {
//            if (userSavedBusStops != null) {
//              Log.d(TAG, "Load user saved bus stops successfully.");
//              //Log.d(TAG, "bus stops: " + userSavedBusStops.size());
//              mNumUserSavedBusStops = userSavedBusStops.size();
//              Log.d(TAG, "Number of stops: " + mNumUserSavedBusStops);
//              controlViewVisibility();
//              if ( userSavedBusStops.size() > 0) {
//                Log.d(TAG, "called");
//                busDepartureViewModel.loadUserFavoriteDepartures(userSavedBusStops);
//              }
//            }
//          },
//          throwable -> Log.e(TAG, "Unable to getFavoriteStopsDepartures user saved bus stops", throwable)));
//    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Timber.d("onResume");
  }

  @Override
  public void onPause() {
    super.onPause();
    Timber.d("onPause has called.");
  }

  @Override
  public void onStop() {
    super.onStop();
    Timber.d("onStop has called.");
    // Log.d(TAG, "Item count : " + mAdapter.getItemCount());
    mDisposable.clear();
    //mRecyclerView.onDetachedFromWindow();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.setting_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_setting:
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public Boolean onBackPressed() {
    return MainNavigationFragment.super.onBackPressed();
  }

  private void initToolbar() {
    Toolbar toolbar = binding.get().toolbarHome;
    toolbar.setTitle("");
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
  }
}
