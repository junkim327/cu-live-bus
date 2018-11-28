package com.example.junyoung.culivebus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.ui.viewmodel.SharedDirectionInfoViewModel;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.httpclient.RetrofitBuilder;
import com.example.junyoung.culivebus.httpclient.services.PlannedTripsServices;
import com.example.junyoung.culivebus.httpclient.pojos.PlannedTrips;
import com.example.junyoung.culivebus.room.entity.RouteInfo;
import com.example.junyoung.culivebus.ui.factory.DirectionViewModelFactory;
import com.example.junyoung.culivebus.ui.Injection;
import com.example.junyoung.culivebus.ui.viewmodel.DirectionInfoViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SharedItineraryViewModel;
import com.example.junyoung.culivebus.util.UtilConnection;
import com.google.android.gms.maps.model.LatLng;

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

  private String placeName;
  private LatLng placeLatLng;
  private Boolean isTopEditText;
  private Boolean isFirstCreated;
  private Boolean mIsInternetConnected = true;

  private String mUid;
  private String mOriginLat;
  private String mOriginLon;
  private String mDestinationLat;
  private String mDestinationLon;

  private Unbinder unbinder;
  private Disposable disposable;
  private ConnectivityManager mConnectivityManager;

  // Callbacks
  private OnInternetConnectedListener mInternetConnectedCallback;
  private onEditTextClickListener mEditTextCallback;
  private onDataRetrievedListener mDataRetrievedCallback;

  // View models
  private DirectionViewModelFactory mViewModelFactory;
  private DirectionInfoViewModel mViewModel;
  private SharedItineraryViewModel mSharedItinararyViewModel;
  private SharedDirectionInfoViewModel mSharedDirectionInfoViewModel;
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  @BindView(R.id.image_button_exit_set_search_locations)
  ImageButton mExitImageButton;
  @BindView(R.id.edittext_top_side_set_search_locations)
  EditText mTopSideEditText;
  @BindView(R.id.edittext_bottom_side_set_search_locations)
  EditText mBottomSideEditText;
  @BindView(R.id.image_button_reverse_set_search_locations)
  ImageButton mReverseButton;
  @BindView(R.id.progressbar_set_search_location)
  ProgressBar mProgressBar;

  public interface onEditTextClickListener {
    public void onEditTextClick(boolean isTopEditText, String hint);
  }

  public interface onDataRetrievedListener {
    public void onDataRetrieved();
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
      mEditTextCallback = (onEditTextClickListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement onEditTextClickListener");
    }

    try {
      mDataRetrievedCallback = (onDataRetrievedListener) context;
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

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );

    // Get uid from shared preferences. If there is no saved uid, then return null.
    mUid = sharedPref.getString(getString(R.string.saved_uid), null);

    mViewModelFactory = Injection.provideDirectionViewModelFactory(getContext());
    mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(DirectionInfoViewModel.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    if (mConnectivityManager != null) {
      mIsInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, true);
    }

    View view = null;
    if (mIsInternetConnected) {
      view = inflater.inflate(R.layout.fragment_direction_searchview, container, false);
      unbinder = ButterKnife.bind(this, view);

      // TAG : 1 for origin, 2 for destination
      mTopSideEditText.setTag(1);
      mTopSideEditText.setEnabled(true);
      mTopSideEditText.setFocusable(false);
      mBottomSideEditText.setTag(2);
      mBottomSideEditText.setEnabled(true);
      mBottomSideEditText.setFocusable(false);
    }

    return view;
  }

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
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mSharedItinararyViewModel = ViewModelProviders.of(getActivity())
      .get(SharedItineraryViewModel.class);
    mSharedDirectionInfoViewModel = ViewModelProviders.of(getActivity())
      .get(SharedDirectionInfoViewModel.class);
  }

  @OnClick(R.id.image_button_exit_set_search_locations)
  public void exitActivity() {
    if (getActivity() != null) {
      NavUtils.navigateUpFromSameTask(getActivity());
    }
  }

  @OnClick({ R.id.edittext_top_side_set_search_locations,
    R.id.edittext_bottom_side_set_search_locations })
  public void startPlaceAutocompleteIntent(EditText editText) {
    if (editText.getId() == R.id.edittext_top_side_set_search_locations) {
      mEditTextCallback.onEditTextClick(true, mTopSideEditText.getHint().toString());
    } else {
      mEditTextCallback.onEditTextClick(false, mBottomSideEditText.getHint().toString());
    }
  }

  public void updatePlaceName(String placeName, boolean isTopEditText) {
    if (isTopEditText) {
      mTopSideEditText.setText(placeName);
    } else {
      mBottomSideEditText.setText(placeName);
    }
  }

  public void updatePlaceLatLng(LatLng placeLatLng, boolean isTopEditText) {
    if (isTopEditText) {
      if (mTopSideEditText.getHint().toString().equals(
        getResources().getString(R.string.edittext_enter_starting_point_hint)
      )) {
        mOriginLat = String.valueOf(placeLatLng.latitude);
        mOriginLon = String.valueOf(placeLatLng.longitude);
      } else {
        mDestinationLat = String.valueOf(placeLatLng.latitude);
        mDestinationLat = String.valueOf(placeLatLng.longitude);
      }
    } else {
      if (mBottomSideEditText.getHint().toString().equals(
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
    mOriginLat = directionInfo.getOriginLat();
    mOriginLon = directionInfo.getOriginLon();
    mDestinationLat = directionInfo.getDestinationLat();
    mDestinationLon = directionInfo.getDestinationLon();
    mTopSideEditText.setText(directionInfo.getStartingPointName());
    mBottomSideEditText.setText(directionInfo.getDestinationName());
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
