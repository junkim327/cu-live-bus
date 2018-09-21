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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.junyoung.uiucbus.Constants;
import com.example.junyoung.uiucbus.MainActivity;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.services.PlannedTripsServices;
import com.example.junyoung.uiucbus.httpclient.pojos.Itinerary;
import com.example.junyoung.uiucbus.httpclient.pojos.PlannedTrips;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class GetDirectionSearchviewFragment extends Fragment {
  public static final String EXTRA_PLACENAME =
    "com.example.junyoung.uiucbus.fragments.EXTRA_PLACENAME";
  public static final String EXTRA_ISTOPEDITTEXT =
    "com.example.junyoung.uiucbus.fragments.EXTRA_ISTOPEDITTEXT";

  private String placeName;
  private LatLng placeLatLng;
  private Boolean isTopEditText;
  private Boolean isFirstCreated;

  private Unbinder unbinder;
  private Disposable disposable;
  private LatLng destinationLatLng;
  private LatLng startingPointLatLng;
  private onEditTextClickListener editTextCallback;
  private onDataRetrievedListener dataRetrievedCallback;

  @BindView(R.id.image_button_exit_set_search_locations)
  ImageButton exitImageButton;
  @BindView(R.id.edittext_top_side_set_search_locations)
  EditText topSideEditText;
  @BindView(R.id.edittext_bottom_side_set_search_locations)
  EditText bottomSideEditText;
  @BindView(R.id.image_button_reverse_set_search_locations)
  ImageButton reversebutton;
  @BindView(R.id.progressbar_set_search_location)
  ProgressBar progressBar;

  public interface onEditTextClickListener {
    public void onEditTextClick(boolean isTopEditText, String hint);
  }

  public interface onDataRetrievedListener {
    public void onDataRetrieved(ArrayList<Itinerary> itineraries);
  }

  public static GetDirectionSearchviewFragment newInstance(String placeName,
                                                           boolean isTopEditText) {
    GetDirectionSearchviewFragment searchviewFragment = new GetDirectionSearchviewFragment();
    Bundle args = new Bundle();
    args.putString(EXTRA_PLACENAME, placeName);
    args.putBoolean(EXTRA_ISTOPEDITTEXT, isTopEditText);
    searchviewFragment.setArguments(args);

    return searchviewFragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    try {
      editTextCallback = (onEditTextClickListener) context;
      dataRetrievedCallback = (onDataRetrievedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement onEditTextClickListener or onDataRetrievedListener");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      isTopEditText = getArguments().getBoolean(EXTRA_ISTOPEDITTEXT);
      placeName = getArguments().getString(EXTRA_PLACENAME);
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_set_search_locations, container, false);
    unbinder = ButterKnife.bind(this, view);

    topSideEditText.setEnabled(true);
    topSideEditText.setFocusable(false);
    bottomSideEditText.setEnabled(true);
    bottomSideEditText.setFocusable(false);

    return view;
  }

  @OnClick(R.id.image_button_reverse_set_search_locations)
  public void reversePoint() {
    String topSideEditTextHint = topSideEditText.getHint().toString();
    String bottomSideEditTextHint = bottomSideEditText.getHint().toString();
    topSideEditText.setHint(bottomSideEditTextHint);
    bottomSideEditText.setHint(topSideEditTextHint);

    String topSideEditTextContent = topSideEditText.getText().toString();
    String bottomSideEditTextContent = bottomSideEditText.getText().toString();
    topSideEditText.setText(bottomSideEditTextContent);
    bottomSideEditText.setText(topSideEditTextContent);

    if (!topSideEditTextContent.contentEquals("") && !bottomSideEditTextContent.equals("")) {
      LatLng temp = startingPointLatLng;
      startingPointLatLng = destinationLatLng;
      destinationLatLng = temp;

      getPlannedTrips();
    }
  }

  @OnClick(R.id.image_button_exit_set_search_locations)
  public void exitActivity() {
    Intent intent = new Intent(getActivity(), MainActivity.class);
    startActivity(intent);
  }

  @OnClick({ R.id.edittext_top_side_set_search_locations,
             R.id.edittext_bottom_side_set_search_locations })
  public void startPlaceAutocompleteIntent(EditText editText) {
    if (editText.getId() == R.id.edittext_top_side_set_search_locations) {
      editTextCallback.onEditTextClick(true, topSideEditText.getHint().toString());
    } else {
      editTextCallback.onEditTextClick(false, bottomSideEditText.getHint().toString());
    }
  }

  public void updatePlaceName(String placeName, boolean isTopEditText) {
    if (isTopEditText) {
      topSideEditText.setText(placeName);
    } else {
      bottomSideEditText.setText(placeName);
    }
  }

  public void updatePlaceLatLng(LatLng placeLatLng, boolean isTopEditText) {
    if (isTopEditText) {
      if (topSideEditText.getHint().toString().equals(
        getResources().getString(R.string.edittext_enter_starting_point_hint)
      )) {
        startingPointLatLng = placeLatLng;
      } else {
        destinationLatLng = placeLatLng;
      }
    } else {
      if (bottomSideEditText.getHint().toString().equals(
        getResources().getString(R.string.edittext_enter_starting_point_hint)
      )) {
        startingPointLatLng = placeLatLng;
      } else {
        destinationLatLng = placeLatLng;
      }
    }

    if (startingPointLatLng != null && destinationLatLng != null) {
      getPlannedTrips();
    }
  }

  private void getPlannedTrips() {
    progressBar.setVisibility(VISIBLE);
    String originLat = String.valueOf(startingPointLatLng.latitude);
    String originLon = String.valueOf(startingPointLatLng.longitude);
    String destinationLat = String.valueOf(destinationLatLng.latitude);
    String destinationLon = String.valueOf(destinationLatLng.longitude);
    PlannedTripsServices plannedTripsService =
      RetrofitBuilder.getRetrofitandRxJavaInstance().create(PlannedTripsServices.class);
    Single<PlannedTrips> source = plannedTripsService.getPlannedTrips(Constants.API_KEY,
                                                                      originLat,
                                                                      originLon,
                                                                      destinationLat,
                                                                      destinationLon);
    disposable = source.subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeWith(new DisposableSingleObserver<PlannedTrips>() {
        @Override
        public void onSuccess(PlannedTrips plannedTrips) {
          progressBar.setVisibility(GONE);
          if (plannedTrips != null) {
            dataRetrievedCallback.onDataRetrieved(plannedTrips.getItineraries());
            Log.d("Data Retrieved", " : Finished");
          }
        }

        @Override
        public void onError(Throwable e) {

        }
      });
    Log.d("Then log is called?", " It should be");
  }

  @Override
  public void onPause() {
    super.onPause();
    if (disposable != null) {
      disposable.dispose();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
