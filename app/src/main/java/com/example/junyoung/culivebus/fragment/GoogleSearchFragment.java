package com.example.junyoung.culivebus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;
import com.example.junyoung.culivebus.adapter.RecentPlaceAdapter;
import com.example.junyoung.culivebus.room.entity.UserPlace;
import com.example.junyoung.culivebus.ui.Injection;
import com.example.junyoung.culivebus.ui.factory.PlaceViewModelFactory;
import com.example.junyoung.culivebus.ui.viewmodel.PlaceViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SharedItineraryViewModel;
import com.example.junyoung.culivebus.util.UtilConnection;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.VISIBLE;

public class GoogleSearchFragment extends Fragment {
  private static final int RESULT_OK = -1;
  private static final int PLACE_PICKER_REQUEST = 66;
  public static final String TAG = GoogleSearchFragment.class.getSimpleName();
  public static final String EXTRA_HINT = "com.example.junyoung.uiucbus.fragments.EXTRA_HINT";

  private String mUid;
  private String hint;

  private Unbinder mUnbinder;
  private PlaceViewModel mViewModel;
  private PlaceViewModelFactory mViewModelFactory;
  private SharedItineraryViewModel mSharedItineraryViewModel;
  private ConnectivityManager mConnectivityManager;
  private RecentPlaceAdapter mAdapter;
  private LatLngBounds champaignUrbanaLatLngBounds;
  private OnInternetConnectedListener mInternetConnectedCallback;
  private OnActivityResultListener onActivityResultCallback;
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  @BindView(R.id.text_header_recent_place)
  TextView mHeaderTextView;
  @BindView(R.id.button_back_google_search)
  ImageButton mBackButton;
  @BindView(R.id.button_choose_on_map_google_search)
  Button chooseOnMapButton;
  @BindView(R.id.recyclerview_google_search)
  RecyclerView mRecyclerView;
  @BindView(R.id.edittext_google_search)
  EditText googleSearchEditText;

  public interface OnActivityResultListener {
    void onActivityResultExecuted(String placeName, LatLng placeLatLng, String hint);
  }

  public static GoogleSearchFragment newInstance(String hint) {
    GoogleSearchFragment searchFragment = new GoogleSearchFragment();
    Bundle args = new Bundle();
    args.putString(EXTRA_HINT, hint);
    searchFragment.setArguments(args);

    return searchFragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    try {
      mInternetConnectedCallback = (OnInternetConnectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnInternetConnectedListener.");
    }

    try {
      onActivityResultCallback = (OnActivityResultListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnActivityResultListener"
      );
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );

    if (getArguments() != null) {
      hint = getArguments().getString(EXTRA_HINT);
    }

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    mUid = sharedPref.getString(getString(R.string.saved_uid), null);

    mViewModelFactory = Injection.providePlaceViewModelFactory(getContext());
    mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PlaceViewModel.class);
    mSharedItineraryViewModel = ViewModelProviders.of(getActivity())
      .get(SharedItineraryViewModel.class);

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
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (mConnectivityManager != null) {
      UtilConnection.isInternetConnected(mConnectivityManager, mInternetConnectedCallback, false);
    }

    View view = inflater.inflate(R.layout.fragment_google_search, container, false);
    mUnbinder = ButterKnife.bind(this, view);

    googleSearchEditText.setHint(hint);
    googleSearchEditText.setFocusable(false);

    setRecyclerView();

    return view;
  }

  public void setRecyclerView() {
    mRecyclerView.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerView.setLayoutManager(layoutManager);

    RecyclerviewClickListener listener = (view, position) -> {
      if (mAdapter != null) {
        UserPlace place = mAdapter.getPlace(position);
        LatLng placeLatLng = new LatLng(place.getLatitude(), place.getLongitude());
        onActivityResultCallback.onActivityResultExecuted(place.getPlaceName(), placeLatLng, hint);
      }
    };

    mAdapter = new RecentPlaceAdapter(getContext(), listener);
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override
  public void onStart() {
    super.onStart();

    if (mUid != null) {
      mDisposable.add(mViewModel.loadAllPlacesByUid(mUid)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(userPlaces -> {
        if (userPlaces != null && userPlaces.size() != 0) {
          mAdapter.setPlaceList(userPlaces);
          mHeaderTextView.setVisibility(VISIBLE);
        }
      }, throwable -> Log.e(TAG, "Unable to update recent places", throwable)));
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mConnectivityManager != null) {
      UtilConnection.isInternetConnected(mConnectivityManager, mInternetConnectedCallback, false);
    }
  }

  @OnClick(R.id.button_back_google_search)
  public void backToPreviousStack() {
    if (getFragmentManager() != null) {
      getFragmentManager().popBackStackImmediate();
    }
  }

  @OnClick(R.id.edittext_google_search)
  public void launchAutoCompleteWidget() {
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 27;
    try {
      if (getActivity() != null) {
        Intent intent =
          new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
            .setBoundsBias(champaignUrbanaLatLngBounds).build(getActivity());
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
      }
    } catch (GooglePlayServicesRepairableException e) {
      GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
      apiAvailability.getErrorDialog(getActivity(), e.getConnectionStatusCode(), PLACE_PICKER_REQUEST);
    } catch (GooglePlayServicesNotAvailableException e) {
      GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
      apiAvailability.getErrorDialog(getActivity(), e.errorCode, PLACE_PICKER_REQUEST);
    }
  }

  @OnClick(R.id.button_choose_on_map_google_search)
  public void launchPlacePickerIntent() {
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

    Log.i(TAG, "Request Code: " + String.valueOf(requestCode)
      + ", Result Code: " + String.valueOf(resultCode));
    Place place = null;
    if (requestCode == 27) {
      if (resultCode == RESULT_OK) {
        if (getContext() != null) {
          place = PlaceAutocomplete.getPlace(getContext(), data);
        }
      }
    } else if (requestCode == PLACE_PICKER_REQUEST) {
      if (resultCode == RESULT_OK) {
        if (getContext() != null) {
          place = PlacePicker.getPlace(getContext(), data);
          Log.d(TAG, "PlacePicker LatLngBounds: " + PlacePicker.getLatLngBounds(data));
        }
      }
    }

    if (place != null) {
      insertPlaceInDatabase(place);

      String placeName = place.getName().toString();
      LatLng placeLatLng = place.getLatLng();
      onActivityResultCallback.onActivityResultExecuted(placeName, placeLatLng, hint);
    }
  }

  private void createUid() {
    mUid = UUID.randomUUID().toString();
    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString(getString(R.string.saved_uid), mUid);
    editor.apply();
  }

  private void insertPlaceInDatabase(Place place) {
    if (mUid == null) {
      LatLng placeLatLng = place.getLatLng();
      mDisposable.add(mViewModel.insertPlace(mUid, placeLatLng.latitude, placeLatLng.longitude,
        place.getName().toString())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> Log.i(TAG, "Place is successfully stored in the database."),
          throwable -> Log.e(TAG, "Unable to insert place :(", throwable)));
    }
  }

  @Override
  public void onStop() {
    super.onStop();

    mDisposable.clear();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    mUnbinder.unbind();
  }
}
