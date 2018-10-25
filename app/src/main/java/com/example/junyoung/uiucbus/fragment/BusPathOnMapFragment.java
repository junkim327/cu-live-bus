package com.example.junyoung.uiucbus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.uiucbus.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.httpclient.pojos.Shape;
import com.example.junyoung.uiucbus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.uiucbus.ui.viewmodel.BusPathViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedDepartureViewModel;
import com.example.junyoung.uiucbus.utils.UtilConnection;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.List;

public class BusPathOnMapFragment extends Fragment implements OnMapReadyCallback{
  private boolean mIsInternetConnected;

  private GoogleMap mMap;

  private ConnectivityManager mConnectivityManager;
  private OnInternetConnectedListener mInternetConnectedCallback;

  private BusPathViewModel mBusPathViewModel;
  private SharedDepartureViewModel mSharedDepartureViewModel;

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

      SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
        .findFragmentById(R.id.map_bus_path);
      mapFragment.getMapAsync(this);
    }

    return view;
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
  }

  private void drawPolyline(List<Shape> shapeList) {
    if (shapeList != null && shapeList.size() > 0) {
      PolylineOptions polylineOptions = new PolylineOptions();
      for (int i = 0; i < shapeList.size(); i++) {
        Shape shape = shapeList.get(i);
        polylineOptions.add(new LatLng(shape.getShapeLat(), shape.getShapeLon()));
      }

      polylineOptions
        .width(20)
        .color(getResources().getColor(R.color.progressbar_color))
        .geodesic(true);

      Polyline polyline = mMap.addPolyline(polylineOptions);
      polyline.setStartCap(new RoundCap());
      polyline.setEndCap(new RoundCap());
      polyline.setJointType(JointType.ROUND);
    }
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mSharedDepartureViewModel = ViewModelProviders.of(getActivity())
      .get(SharedDepartureViewModel.class);
    SortedDeparture departure = mSharedDepartureViewModel.getDeparture().getValue();

    mBusPathViewModel = ViewModelProviders.of(this).get(BusPathViewModel.class);
    if (departure != null) {
      mBusPathViewModel.init(departure.getTripList().get(0).getShapeId());
    }
    mBusPathViewModel.getBusPath().observe(this, path -> {
      // Update UI
      if (path != null) {
        drawPolyline(path.getShapes());
      }
    });
  }
}
