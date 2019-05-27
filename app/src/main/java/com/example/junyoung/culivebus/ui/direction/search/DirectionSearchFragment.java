package com.example.junyoung.culivebus.ui.direction.search;

import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.binding.FragmentDataBindingComponent;
import com.example.junyoung.culivebus.databinding.DirectionSearchFragmentBinding;
import com.example.junyoung.culivebus.di.Injectable;
import com.example.junyoung.culivebus.ui.common.DirectionNavigationController;
import com.example.junyoung.culivebus.ui.common.NavigationController;
import com.example.junyoung.culivebus.ui.direction.SharedPlaceViewModel;
import com.example.junyoung.culivebus.util.AutoClearedValue;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.adapter.RecentPlaceAdapter;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class DirectionSearchFragment extends DaggerFragment {
  private static final int PLACE_AUTOCOMPLETE_REQUEST = 27;
  private static final int PLACE_PICKER_REQUEST = 1996;

  @Inject
  ViewModelProvider.Factory viewModelFactory;
  @Inject
  NavigationController navigationController;

  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<DirectionSearchFragmentBinding> binding;
  AutoClearedValue<SearchedPlaceListAdapter> adapter;

  private DirectionSearchViewModel directionSearchViewModel;
  private SharedPlaceViewModel sharedPlaceViewModel;

  private LatLngBounds champaignUrbanaLatLngBounds;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LatLng champaignSouthWest = new LatLng(
      Double.valueOf(getString(R.string.champaign_south_west_lat)),
      Double.valueOf(getString(R.string.champaign_south_west_lon))
    );
    LatLng urbanaNorthEast = new LatLng(
      Double.valueOf(getString(R.string.urbana_north_east_lat)),
      Double.valueOf(getString(R.string.urbana_north_east_lon))
    );
    champaignUrbanaLatLngBounds = new LatLngBounds(champaignSouthWest, urbanaNorthEast);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    directionSearchViewModel = ViewModelProviders.of(this, viewModelFactory)
      .get(DirectionSearchViewModel.class);
    sharedPlaceViewModel = ViewModelProviders.of(getActivity(), viewModelFactory)
      .get(SharedPlaceViewModel.class);
    DirectionSearchFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.fragment_direction_search, container, false, dataBindingComponent);
    dataBinding.setLifecycleOwner(this);
    dataBinding.setSharedPlaceViewModel(sharedPlaceViewModel);
    dataBinding.setDirectionSearchViewModel(directionSearchViewModel);

    dataBinding.upButton.setOnClickListener(view -> {
      FragmentManager fm = getFragmentManager();
      if (fm != null && fm.getBackStackEntryCount() > 0) {
        fm.popBackStack();
      }
    });
    dataBinding.googleSearchButton.setOnClickListener(view -> launchAutoCompleteWidget());
    dataBinding.chooseOnMapButton.setOnClickListener(view -> launchPlacePickerIntent());
    dataBinding.moreSearchHistoryButton.setOnClickListener(view ->
      navigationController.navigateToSearchHistory()
    );

    binding = new AutoClearedValue<>(this, dataBinding);

    return dataBinding.getRoot();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    initRecyclerView();

    SearchedPlaceListAdapter rvAdapter = new SearchedPlaceListAdapter(dataBindingComponent,
      searchedPlace -> {
        sharedPlaceViewModel.setPlace(searchedPlace);
        navigationController.navigateToDirection();
      });
    binding.get().searchedPlaceList.setAdapter(rvAdapter);
    adapter = new AutoClearedValue<>(this, rvAdapter);
  }

  private void initRecyclerView() {
    directionSearchViewModel.getSearchedPlaces().observe(this, listResource -> {
      Timber.d("Observe");
      if (listResource == null || listResource.data == null) {
        directionSearchViewModel.setResultCount(0);
        adapter.get().replace(null);
      } else {
        directionSearchViewModel.setResultCount(listResource.data.size());
        adapter.get().replace((listResource.data.size() > 7) ? listResource.data.subList(0, 7)
          : listResource.data);
      }
    });
  }

  private void launchAutoCompleteWidget() {
    try {
      if (getActivity() != null) {
        Intent intent =
          new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
            .setBoundsBias(champaignUrbanaLatLngBounds).build(getActivity());
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST);
      }
    } catch (GooglePlayServicesRepairableException e) {
      GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
      apiAvailability.getErrorDialog(getActivity(), e.getConnectionStatusCode(), PLACE_PICKER_REQUEST);
    } catch (GooglePlayServicesNotAvailableException e) {
      GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
      apiAvailability.getErrorDialog(getActivity(), e.errorCode, PLACE_PICKER_REQUEST);
    }
  }

  private void launchPlacePickerIntent() {
    try {
      if (getActivity() != null) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent intent = builder.build(getActivity());
        startActivityForResult(intent, PLACE_PICKER_REQUEST);
        // TODO: If user location is turned off, then set latlngbounds to illini union.
      }
    } catch (GooglePlayServicesRepairableException e) {
      GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
      apiAvailability.getErrorDialog(getActivity(), e.getConnectionStatusCode(), PLACE_PICKER_REQUEST);
    } catch (GooglePlayServicesNotAvailableException e) {
      GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
      apiAvailability.getErrorDialog(getActivity(), e.errorCode, PLACE_PICKER_REQUEST);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    Place place = null;
    if (requestCode == PLACE_AUTOCOMPLETE_REQUEST && resultCode == RESULT_OK && getContext() != null) {
      place = PlaceAutocomplete.getPlace(getContext(), data);
    } else if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK
      && getContext() != null) {
      place = PlacePicker.getPlace(getContext(), data);
      Timber.d("PlacePicker LatLngBounds: %s", PlacePicker.getLatLngBounds(data));
    }

    if (place != null) {
      Timber.d("Location Id: %s", place.getId());
      Timber.d("LatLng: (%s, %s)", place.getLatLng().latitude, place.getLatLng()
        .longitude);
      directionSearchViewModel.insertSearchedPlace(place);
      sharedPlaceViewModel.setPlace(place);
      navigationController.navigateToDirection();
    }
  }
}
