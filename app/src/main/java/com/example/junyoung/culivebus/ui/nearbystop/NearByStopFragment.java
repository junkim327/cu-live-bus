package com.example.junyoung.culivebus.ui.nearbystop;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.adapter.BusStopInfoWindowAdapter;
import com.example.junyoung.culivebus.httpclient.pojos.Stop;
import com.example.junyoung.culivebus.room.entity.StopPoint;
import com.example.junyoung.culivebus.ui.viewmodel.BusStopViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SharedStopPointViewModel;
import com.example.junyoung.culivebus.util.UtilConnection;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.culivebus.vo.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;

public class NearByStopFragment extends Fragment implements OnMapReadyCallback,
  GoogleMap.OnCameraIdleListener,
  GoogleMap.OnMarkerClickListener,
  GoogleMap.OnInfoWindowClickListener,
  GoogleMap.OnMyLocationButtonClickListener {
  private static final String TAG = NearByStopFragment.class.getSimpleName();

  private int mToolbarHeight;
  private String mUserLatitude;
  private String mUserLongitude;
  private boolean mIsInternetConnected;

  private StopPoint selectedStopPoint;

  private GoogleMap mMap;
  private Unbinder mUnbinder;
  private ConnectivityManager mConnectivityManager;
  private BottomSheetBehavior mBottomSheetBehavior;

  // ViewModels
  private BusStopViewModel mBusStopViewModel;
  private SharedStopPointViewModel mSharedStopPointViewModel;

  // Callback
  private OnBusStopClickedListener mOnBusStopClickedCallback;
  private OnInternetConnectedListener mInternetConnectedCallback;

  @BindView(R.id.toolbar_near_by_stop)
  Toolbar mToolbar;

  // Bottom Sheet Components
  @BindView(R.id.bottom_sheet_near_by_stop)
  ConstraintLayout mBottomSheet;
  @BindView(R.id.textview_bus_stop_name_bottom_sheet)
  TextView mBusStopNameTextView;
  @BindView(R.id.textview_bus_stop_code_bottom_sheet)
  TextView mBusStopCodeTextView;
  @BindView(R.id.button_bus_departures_bottom_sheet)
  ImageButton mBusDepartureButton;

  public interface OnBusStopClickedListener {
    void onBusStopClicked();
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
      mOnBusStopClickedCallback = (OnBusStopClickedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnBusStopClickedListener.");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getActivity() != null) {
      mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
        Context.CONNECTIVITY_SERVICE
      );
    }

    mToolbarHeight = getResources().getDimensionPixelSize(R.dimen.small_size_toolbar_height);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    checkInternetConnection();

    View view = null;
    if (mIsInternetConnected) {
      view = inflater.inflate(R.layout.fragment_near_by_stop, container, false);
      mUnbinder = ButterKnife.bind(this, view);

      setToolbar();
      loadMapFragment();
      setBottomSheetBehavior();
    }

    return view;
  }

  private void checkInternetConnection() {
    mIsInternetConnected = true;
    if (mConnectivityManager != null) {
      mIsInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, false);
    }
  }

  private void setToolbar() {
    mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

    if (getActivity() != null) {
      ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

      ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
      if (actionBar != null) {
        setHasOptionsMenu(true);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        actionBar.setTitle(getResources().getString(R.string.toolbar_title_near_by_stop_fragment));
      }
    }
  }

  private void setBottomSheetBehavior() {
    mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
    mBottomSheetBehavior.setPeekHeight(0);
    mBottomSheetBehavior.setState(STATE_COLLAPSED);
  }

  private void loadMapFragment() {
    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
      .findFragmentById(R.id.map_near_by_stop);
    if (mapFragment != null) {
      mapFragment.getMapAsync(this);
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    mMap.setPadding(0, mToolbarHeight, 0, 0);

    BusStopInfoWindowAdapter infoWindowAdapter = new BusStopInfoWindowAdapter(getActivity());
    mMap.setInfoWindowAdapter(infoWindowAdapter);

    drawOnTheMap();

    mMap.setOnCameraIdleListener(this);
    mMap.setOnMarkerClickListener(this);
    mMap.setOnInfoWindowClickListener(this);
    mMap.setOnMyLocationButtonClickListener(this);
  }

  private void drawOnTheMap() {
    mUserLatitude = getResources().getString(R.string.illini_union_latitude);
    mUserLongitude = getResources().getString(R.string.illini_union_longitude);

    if (getContext() != null && ContextCompat.checkSelfPermission(getContext(),
      Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      mMap.setMyLocationEnabled(true);

      if (getActivity() != null) {
        FusedLocationProviderClient fusedLocationProviderClient =
          LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(),
          location -> {
            if (location != null) {
              mUserLatitude = String.valueOf(location.getLatitude());
              mUserLongitude = String.valueOf(location.getLongitude());

              mBusStopViewModel.loadNearBusStopList(mUserLatitude, mUserLongitude);
              animateCamera();
            }
          });
      }
    } else {
      mBusStopViewModel.loadNearBusStopList(mUserLatitude, mUserLongitude);
      animateCamera();
    }
  }

  private void animateCamera() {
    if (mUserLatitude != null && mUserLongitude != null) {
      mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
        new LatLng(Double.valueOf(mUserLatitude), Double.valueOf(mUserLongitude)), 16.0f),
        500, null);
    }
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mBusStopViewModel = ViewModelProviders.of(this).get(BusStopViewModel.class);
    mBusStopViewModel.getBusStopList().observe(this, this::processResponse);

    if (getActivity() != null) {
      mSharedStopPointViewModel = ViewModelProviders.of(getActivity())
        .get(SharedStopPointViewModel.class);
    }
  }

  private void processResponse(Response<List<Stop>> response) {
    switch (response.mStatus) {
      case SUCCESS:
        if (response.mData != null) {
          createBusStopMarkers(response.mData);
        }
        break;
      case ERROR:
        break;
    }
  }

  private void createBusStopMarkers(List<Stop> busStopList) {
    MarkerOptions options = new MarkerOptions()
      .icon(BitmapDescriptorFactory.fromResource(R.drawable.google_map_custom_marker));

    for (int i = 0; i < busStopList.size(); i++) {
      ArrayList<StopPoint> stopPoints = busStopList.get(i).getStopPoints();
      for (StopPoint stopPoint : stopPoints) {
        LatLng markerPosition = new LatLng(
          stopPoint.getLatitude(),
          stopPoint.getLongitude()
        );

        Marker marker = mMap.addMarker(options.position(markerPosition));
        marker.setTag(stopPoint);
      }
    }

    /*
    if (!isFragmentVisible) {
      isFragmentVisible = true;
      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentByTag(MAP_FRAGMENT_TAG);
      FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
      fragmentTransaction.show(mapFragment);
      fragmentTransaction.commit();
    }*/
  }

  @Override
  public void onResume() {
    super.onResume();

    checkInternetConnection();

    mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
      @Override
      public void onStateChanged(@NonNull View view, int i) {

      }

      @Override
      public void onSlide(@NonNull View view, float v) {
        int bottomSheetHeight = mBottomSheet.getHeight();
        if (bottomSheetHeight != 0) {
          int bottomPadding = (int) (bottomSheetHeight * v);
          mMap.setPadding(0, mToolbarHeight, 0, bottomPadding);
        }
      }
    });
  }

  @Override
  public void onCameraIdle() {
    Log.d(TAG, "onCameraIdle has called");
    LatLng cameraCurrentLatLng = mMap.getCameraPosition().target;

    mBusStopViewModel.loadNearBusStopList(
      String.valueOf(cameraCurrentLatLng.latitude),
      String.valueOf(cameraCurrentLatLng.longitude)
    );
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    // If the user marker clicked, then don't open the info window.
    /*
    if (marker.getId().equals(userMarkerId)) {
      marker.hideInfoWindow();
      return true;
    }*/

    selectedStopPoint = (StopPoint) marker.getTag();

    if (selectedStopPoint != null) {
      mBusStopNameTextView.setText(selectedStopPoint.getStopName());
      mBusStopCodeTextView.setText(selectedStopPoint.getStopCode());
    }

    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    return false;
  }

  @Override
  public void onInfoWindowClick(Marker marker) {
    StopPoint stopPoint = (StopPoint) marker.getTag();
    if (stopPoint != null) {
      mSharedStopPointViewModel.select(stopPoint);
    }
    mOnBusStopClickedCallback.onBusStopClicked();
  }

  @Override
  public boolean onMyLocationButtonClick() {
    return false;
  }

  @OnClick(R.id.button_bus_departures_bottom_sheet)
  public void clickBusDeparturesButton() {
    if (selectedStopPoint != null) {
      mSharedStopPointViewModel.select(selectedStopPoint);
    }
    mOnBusStopClickedCallback.onBusStopClicked();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        if (getActivity() != null) {
          NavUtils.navigateUpFromSameTask(getActivity());
        }

        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    if (mUnbinder != null) {
      mUnbinder.unbind();
      mUnbinder = null;
    }
  }
}
