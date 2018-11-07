package com.example.junyoung.uiucbus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.example.junyoung.uiucbus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.httpclient.pojos.Shape;
import com.example.junyoung.uiucbus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.uiucbus.ui.viewmodel.BusPathViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedDepartureViewModel;
import com.example.junyoung.uiucbus.util.UtilConnection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BusPathOnMapFragment extends Fragment implements OnMapReadyCallback,
  GoogleMap.OnMapLoadedCallback {
  private boolean mIsInternetConnected;
  private String mRouteColor;

  private SortedDeparture mDeparture;

  private GoogleMap mMap;
  private Unbinder mUnbinder;
  private ConnectivityManager mConnectivityManager;
  private OnInternetConnectedListener mInternetConnectedCallback;

  private BusPathViewModel mBusPathViewModel;
  private SharedDepartureViewModel mSharedDepartureViewModel;

  @BindView(R.id.toolbar_bus_path_on_map)
  Toolbar mToolbar;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    try {
      mInternetConnectedCallback = (OnInternetConnectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnInternetConnectedListener.");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );

    mSharedDepartureViewModel = ViewModelProviders.of(getActivity())
      .get(SharedDepartureViewModel.class);
    mDeparture = mSharedDepartureViewModel.getDeparture().getValue();
    if (mDeparture != null) {
      mRouteColor = mDeparture.getRouteList().get(0).getRouteColor();
    }

    mBusPathViewModel = ViewModelProviders.of(this).get(BusPathViewModel.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    mIsInternetConnected = true;
    if (mConnectivityManager != null) {
      mIsInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, false);
    }

    View view = null;
    if (mIsInternetConnected) {
      view = inflater.inflate(R.layout.activity_bus_path_on_map, container, false);
      mUnbinder = ButterKnife.bind(this, view);

      setToolbar();

      SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
        .findFragmentById(R.id.map_bus_path);
      mapFragment.getMapAsync(this);
    }

    return view;
  }

  private void setToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      setHasOptionsMenu(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    if (mDeparture != null) {
      Log.d("BusPath", mDeparture.getTripList().get(0).getShapeId());
      mBusPathViewModel.init(mDeparture.getTripList().get(0).getShapeId());
    }
  }

  @Override
  public void onMapLoaded() {

  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mBusPathViewModel.getBusPath().observe(this, path -> {
      // Update UI
      if (path != null) {
        LatLngBounds bound = drawPolyline(path.getShapes());
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
          @Override
          public void onMapLoaded() {
            if (bound != null) {
              mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, 5));
            }
          }
        });
      }
    });
  }

  private LatLngBounds drawPolyline(List<Shape> shapeList) {
    if (shapeList != null && shapeList.size() > 0) {
      LatLngBounds.Builder builder = LatLngBounds.builder();
      PolylineOptions polylineOptions = new PolylineOptions();
      for (int i = 0; i < shapeList.size(); i++) {
        Shape shape = shapeList.get(i);
        LatLng point = new LatLng(shape.getShapeLat(), shape.getShapeLon());
        if (shape.getStopId() != null) {
          mMap.addMarker(new MarkerOptions()
            .position(point)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.google_map_custom_marker))
          );
        }
        builder.include(point);
        polylineOptions.add(point);
      }

      polylineOptions
        .width(20)
        .color(Color.parseColor(getString(R.string.hex_color, mRouteColor)))
        .geodesic(true);

      Polyline polyline = mMap.addPolyline(polylineOptions);
      polyline.setStartCap(new RoundCap());
      polyline.setEndCap(new RoundCap());
      polyline.setJointType(JointType.ROUND);

      return builder.build();
    }

    return null;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.bus_route_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    mUnbinder.unbind();
  }
}
