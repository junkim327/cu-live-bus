package com.example.junyoung.culivebus.ui.direction.result;

import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.android.support.DaggerFragment;
import timber.log.Timber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.binding.FragmentDataBindingComponent;
import com.example.junyoung.culivebus.databinding.DirectionResultFragmentBinding;
import com.example.junyoung.culivebus.ui.common.NavigationController;
import com.example.junyoung.culivebus.ui.direction.SharedPlaceViewModel;
import com.example.junyoung.culivebus.util.AutoClearedValue;
import com.example.junyoung.culivebus.R;


import javax.inject.Inject;

public class DirectionResultFragment extends DaggerFragment {
  @Inject
  ViewModelProvider.Factory viewModelFactory;
  @Inject
  NavigationController navigationController;

  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<DirectionResultFragmentBinding> binding;
  AutoClearedValue<DirectionResultListAdapter> adapter;

  private SharedPlaceViewModel sharedPlaceViewModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    sharedPlaceViewModel = ViewModelProviders.of(getActivity(), viewModelFactory)
      .get(SharedPlaceViewModel.class);
    DirectionResultFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.fragment_direction_result, container, false, dataBindingComponent);
    dataBinding.setLifecycleOwner(this);
    dataBinding.setSharedPlaceViewModel(sharedPlaceViewModel);

    dataBinding.startingPointEditText.setOnClickListener(view -> {
        sharedPlaceViewModel.setIsStartingPointEditTextClicked(true);
        navigationController.navigateToDirectionSearch();
      }
    );
    dataBinding.destinationEditText.setOnClickListener(view -> {
        sharedPlaceViewModel.setIsStartingPointEditTextClicked(false);
        navigationController.navigateToDirectionSearch();
      }
    );

    binding = new AutoClearedValue<>(this, dataBinding);

    return dataBinding.getRoot();
  }

  /*
  @OnClick(R.id.image_button_reverse_set_search_locations)
  public void reversePoint() {
    int tag = (int) mTopSideEditText.getTag();
    mTopSideEditText.setTag(mBottomSideEditText.getTag());
    mBottomSideEditText.setTag(tag);

    String topSideEditTextContent = mTopSideEditText.getText().toString();
    String bottomSideEditTextContent = mBottomSideEditText.getText().toString();
    mTopSideEditText.setText(bottomSideEditTextContent);
    mBottomSideEditText.setText(topSideEditTextContent);

    if (!topSideEditTextContent.contentEquals("") && !bottomSideEditTextContent.equals("")) {
      String tempLat = mOriginLat;
      String tempLon = mOriginLon;
      mOriginLat = mDestinationLat;
      mOriginLon = mDestinationLon;
      mDestinationLat = tempLat;
      mDestinationLon = tempLon;

      getPlannedTrips();
    }
  }*/

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    initRecyclerView();

    DirectionResultListAdapter rvAdapter = new DirectionResultListAdapter(dataBindingComponent,
      itinerary -> {});
    binding.get().directionResultList.setAdapter(rvAdapter);
    adapter = new AutoClearedValue<>(this, rvAdapter);
  }

  private void initRecyclerView() {
    sharedPlaceViewModel.getItineraries().observe(this, listResource -> {
      Timber.d("onChanged called");
      adapter.get().updateItineraries(listResource == null ? null : listResource.data);
    });
  }

  /*



  private void getPlannedTrips() {
    if (mConnectivityManager != null) {
      mIsInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, true);
    }

    if (mIsInternetConnected) {
      mProgressBar.setVisibility(VISIBLE);

      String startingPointName = mTopSideEditText.getText().toString();
      String destinationName = mBottomSideEditText.getText().toString();

      mSharedDirectionInfoViewModel.select(new RouteInfo(mOriginLat, mOriginLon,
        startingPointName, mDestinationLat, mDestinationLon, destinationName));

      mDisposable.add(mViewModel.insertRouteInfo(mUid, mOriginLat, mOriginLon, startingPointName,
        mDestinationLat, mDestinationLon, destinationName)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> Log.i(TAG, "Route info is successfully stored in the database."),
          throwable -> Log.e(TAG, "Unable to insert route info :(", throwable)));

      PlannedTripsServices plannedTripsService =
        RetrofitBuilder.getRetrofitandRxJavaInstance().create(PlannedTripsServices.class);
      Single<PlannedTripsResponse> source = plannedTripsService.getPlannedTrips(Constants.API_KEY,
        mOriginLat,
        mOriginLon,
        mDestinationLat,
        mDestinationLon);
      disposable = source.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<PlannedTripsResponse>() {
          @Override
          public void onSuccess(PlannedTripsResponse plannedTrips) {
            mProgressBar.setVisibility(GONE);
            if (plannedTrips != null) {
              mSharedItinararyViewModel.select(plannedTrips.getItineraries());
              mDataRetrievedCallback.onDataRetrieved();
              Log.d("Data Retrieved", " : Finished");
            }
          }

          @Override
          public void onError(Throwable e) {

          }
        });
      Log.d("Then log is called?", " It should be");
    }
  }

  public void updateDirectionInfo(RouteInfo directionInfo) {
    mOriginLat = directionInfo.getStartingPointLat();
    mOriginLon = directionInfo.getStartingPointLon();
    mDestinationLat = directionInfo.getDestinationLat();
    mDestinationLon = directionInfo.getDestinationLon();
    mTopSideEditText.setText(directionInfo.getStartingPointName());
    mBottomSideEditText.setText(directionInfo.getDestinationName());
    getPlannedTrips();
  } */

  @Override
  public void onResume() {
    super.onResume();
  }

}
