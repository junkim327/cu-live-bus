package com.example.junyoung.uiucbus.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.junyoung.uiucbus.R;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoogleSearchFragment extends Fragment {
  private static final int RESULT_OK = -1;
  private static final int PLACE_PICKER_REQUEST = 66;
  public static final String TAG = "GoogleSearchFragment";
  public static final String EXTRA_HINT = "com.example.junyoung.uiucbus.fragments.EXTRA_HINT";

  private String hint;
  private OnActivityResultListener onActivityResultCallback;

  @BindView(R.id.button_google_search)
  ImageButton googleSearchButton;
  @BindView(R.id.edittext_google_search)
  EditText googleSearchEditText;
  @BindView(R.id.button_choose_on_map_google_search)
  Button chooseOnMapButton;

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
    if (getArguments() != null) {
      hint = getArguments().getString(EXTRA_HINT);
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_google_search, container, false);
    ButterKnife.bind(this, view);

    googleSearchEditText.setHint(hint);
    googleSearchEditText.setFocusable(false);

    return view;
  }

  @OnClick({R.id.button_google_search, R.id.edittext_google_search})
  public void launchAutoCompleteWidget() {
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 27;
    try {
      if (getActivity() != null) {
        Intent intent =
          new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
            .build(getActivity());
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
      PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
      if (getActivity() != null) {
        startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
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
    if (requestCode == 27) {
      if (resultCode == RESULT_OK) {
        if (getContext() != null) {
          Place place = PlaceAutocomplete.getPlace(getContext(), data);
          String placeName = place.getName().toString();
          LatLng placeLatLng = place.getLatLng();
          onActivityResultCallback.onActivityResultExecuted(placeName, placeLatLng, hint);
        }
      }
    } else if (requestCode == PLACE_PICKER_REQUEST) {
      if (resultCode == RESULT_OK) {
        if (getContext() != null) {
          Place place = PlacePicker.getPlace(getContext(), data);
          String placeName = place.getName().toString();
          LatLng placeLatLng = place.getLatLng();
          onActivityResultCallback.onActivityResultExecuted(placeName, placeLatLng, hint);
        }
      }
    }
  }
}
