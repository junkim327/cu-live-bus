package com.example.junyoung.uiucbus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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
import com.example.junyoung.uiucbus.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.services.PlannedTripsServices;
import com.example.junyoung.uiucbus.httpclient.pojos.Itinerary;
import com.example.junyoung.uiucbus.httpclient.pojos.PlannedTrips;
import com.example.junyoung.uiucbus.room.entity.RouteInfo;
import com.example.junyoung.uiucbus.ui.factory.DirectionViewModelFactory;
import com.example.junyoung.uiucbus.ui.Injection;
import com.example.junyoung.uiucbus.ui.viewmodel.RouteInfoViewModel;
import com.example.junyoung.uiucbus.util.UtilConnection;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DirectionSearchviewFragment extends Fragment {
  private static final String TAG = DirectionSearchviewFragment.class.getSimpleName();
  public static final String EXTRA_PLACENAME =
    "com.example.junyoung.uiucbus.fragments.EXTRA_PLACENAME";
  public static final String EXTRA_ISTOPEDITTEXT =
    "com.example.junyoung.uiucbus.fragments.EXTRA_ISTOPEDITTEXT";

  private String placeName;
  private LatLng placeLatLng;
  private Boolean isTopEditText;
  private Boolean isFirstCreated;

  private String mUid;
  private String mOriginLat;
  private String mOriginLon;
  private String mDestinationLat;
  private String mDestinationLon;

  private Unbinder unbinder;
  private Disposable disposable;
  private ConnectivityManager mConnectivityManager;
  private OnInternetConnectedListener mInternetConnectedCallback;
  private onEditTextClickListener editTextCallback;
  private onDataRetrievedListener dataRetrievedCallback;
  private DirectionViewModelFactory mViewModelFactory;
  private RouteInfoViewModel mViewModel;
  private final CompositeDisposable mDisposable = new CompositeDisposable();

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

  public static DirectionSearchviewFragment newInstance(String placeName,
                                                        boolean isTopEditText) {
    DirectionSearchviewFragment searchviewFragment = new DirectionSearchviewFragment();
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
      mInternetConnectedCallback = (OnInternetConnectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnRecentDirectionClickListener.");
    }

    try {
      editTextCallback = (onEditTextClickListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement onEditTextClickListener");
    }

    try {
      dataRetrievedCallback = (onDataRetrievedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement onDataRetrievedListener");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );

    if (getArguments() != null) {
      isTopEditText = getArguments().getBoolean(EXTRA_ISTOPEDITTEXT);
      placeName = getArguments().getString(EXTRA_PLACENAME);
    }

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );

    // Get uid from shared preferences. If there is no saved uid, then return null.
    mUid = sharedPref.getString(getString(R.string.saved_uid), null);

    mViewModelFactory = Injection.provideDirectionViewModelFactory(getContext());
    mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(RouteInfoViewModel.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_direction_searchview, container, false);
    unbinder = ButterKnife.bind(this, view);

    // TAG : 1 for origin, 2 for destination
    topSideEditText.setTag(1);
    topSideEditText.setEnabled(true);
    topSideEditText.setFocusable(false);
    bottomSideEditText.setTag(2);
    bottomSideEditText.setEnabled(true);
    bottomSideEditText.setFocusable(false);

    return view;
  }

  @OnClick(R.id.image_button_reverse_set_search_locations)
  public void reversePoint() {
    int tag = (int) topSideEditText.getTag();
    topSideEditText.setTag(bottomSideEditText.getTag());
    bottomSideEditText.setTag(tag);

    String topSideEditTextContent = topSideEditText.getText().toString();
    String bottomSideEditTextContent = bottomSideEditText.getText().toString();
    topSideEditText.setText(bottomSideEditTextContent);
    bottomSideEditText.setText(topSideEditTextContent);

    if (!topSideEditTextContent.contentEquals("") && !bottomSideEditTextContent.equals("")) {
      String tempLat = mOriginLat;
      String tempLon = mOriginLon;
      mOriginLat = mDestinationLat;
      mOriginLon = mDestinationLon;
      mDestinationLat = tempLat;
      mDestinationLon = tempLon;

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
        mOriginLat = String.valueOf(placeLatLng.latitude);
        mOriginLon = String.valueOf(placeLatLng.longitude);
      } else {
        mDestinationLat = String.valueOf(placeLatLng.latitude);
        mDestinationLat = String.valueOf(placeLatLng.longitude);
      }
    } else {
      if (bottomSideEditText.getHint().toString().equals(
        getResources().getString(R.string.edittext_enter_starting_point_hint)
      )) {
        mOriginLat = String.valueOf(placeLatLng.latitude);
        mOriginLon = String.valueOf(placeLatLng.longitude);
      } else {
        mDestinationLat = String.valueOf(placeLatLng.latitude);
        mDestinationLon = String.valueOf(placeLatLng.longitude);
      }
    }

    if (mOriginLat != null && mDestinationLat != null) {
      getPlannedTrips();
    }
  }

  private void getPlannedTrips() {
    boolean isConnected = true;
    if (mConnectivityManager != null) {
      isConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, true);
    }

    if (isConnected) {
      progressBar.setVisibility(VISIBLE);

      String startingPointName = topSideEditText.getText().toString();
      String destinationName = bottomSideEditText.getText().toString();

      mDisposable.add(mViewModel.insertRouteInfo(mUid, mOriginLat, mOriginLon, startingPointName,
        mDestinationLat, mDestinationLon, destinationName)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> Log.i(TAG, "Route info is successfully stored in the database."),
          throwable -> Log.e(TAG, "Unable to insert route info :(", throwable)));

      PlannedTripsServices plannedTripsService =
        RetrofitBuilder.getRetrofitandRxJavaInstance().create(PlannedTripsServices.class);
      Single<PlannedTrips> source = plannedTripsService.getPlannedTrips(Constants.API_KEY,
        mOriginLat,
        mOriginLon,
        mDestinationLat,
        mDestinationLon);
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
  }

  public void updateDirectionInfo(RouteInfo directionInfo) {
    mOriginLat = directionInfo.getOriginLat();
    mOriginLon = directionInfo.getOriginLon();
    mDestinationLat = directionInfo.getDestinationLat();
    mDestinationLon = directionInfo.getDestinationLon();
    topSideEditText.setText(directionInfo.getStartingPointName());
    bottomSideEditText.setText(directionInfo.getDestinationName());
    getPlannedTrips();
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume has called");
  }

  @Override
  public void onStop() {
    super.onStop();
    mDisposable.clear();
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
