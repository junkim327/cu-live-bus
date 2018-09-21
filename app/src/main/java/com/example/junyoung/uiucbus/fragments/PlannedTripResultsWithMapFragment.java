package com.example.junyoung.uiucbus.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.example.junyoung.uiucbus.DeviceDimensionsHelper;
import com.example.junyoung.uiucbus.MainActivity;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.httpclient.pojos.Walk;
import com.example.junyoung.uiucbus.utils.TimeFormatter;
import com.example.junyoung.uiucbus.adapters.BusInfoInBottomSheetAdapter;
import com.example.junyoung.uiucbus.adapters.TripInfoInBottomSheetAdapter;
import com.example.junyoung.uiucbus.httpclient.pojos.Itinerary;
import com.example.junyoung.uiucbus.httpclient.pojos.Leg;
import com.example.junyoung.uiucbus.httpclient.pojos.Path;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PlannedTripResultsWithMapFragment extends Fragment {
  public static final String EXTRA_BUS =
    "com.example.junyoung.uiucbus.fragments.EXTRA_BUS";
  public static final String EXTRA_WALK =
    "com.example.junyoung.uiucbus.fragments.EXTRA_WALK";
  public static final String EXTRA_PATH =
    "com.example.junyoung.uiucbus.fragments.EXTRA_PATH";
  public static final String EXTRA_ITINERARY =
    "com.example.junyoung.uiucbus.fragments.EXTRA_ITINERARY";
  public static final String EXTRA_STARTPOINTNAME =
    "com.example.junyoung.uiucbus.fragments.EXTRA_STARTPOINTNAME";
  public static final String EXTRA_DESTINATIONNAME =
    "com.example.junyoung.uiucbus.fragments.EXTRA_DESTINATIONNAME";

  private int toolbarBottom;
  private int bottomSheetBottom;
  private String startPointName;
  private String destinationName;
  private ArrayList<Leg> busList = null;
  private ArrayList<Leg> walkList = null;
  private ArrayList<Path> pathList = null;
  private ArrayList<ArrayList<LatLng>> latLngList= null;

  private GoogleMap map;
  private Unbinder unbinder;
  private Itinerary itinerary;
  private LatLngBounds latLngBounds;

  @BindView(R.id.toolbar_planned_trips_result_with_map)
  Toolbar toolbar;
  @BindView(R.id.textview_walk_time_bottom_sheet_planned_trip_results)
  TextView walkTimeTextView;
  @BindView(R.id.textview_travel_time_bottom_sheet_planned_trip_results)
  TextView travelTimeTextView;
  @BindView(R.id.textview_time_interval_bottom_sheet_planned_trip_results)
  TextView timeIntervalTextView;

  @BindView(R.id.constraint_layout_planned_trip_results)
  ConstraintLayout tripInfoSubLayout;
  @BindView(R.id.bottom_sheet_in_planned_trip_results_with_map_fragment)
  ConstraintLayout bottomSheet;
  @BindView(R.id.recyclerview_bus_info_bottom_sheet_planned_trip_results)
  RecyclerView busInfoRecyclerView;
  @BindView(R.id.recyclerview_planned_trip_info_bottom_sheet_planned_trip_results)
  RecyclerView tripInfoRecyclerView;

  public static PlannedTripResultsWithMapFragment newInstance(Itinerary itinerary,
                                                              ArrayList<Leg> busList,
                                                              ArrayList<Leg> walkList,
                                                              ArrayList<Path> pathList,
                                                              String startPointName,
                                                              String destinationName) {
    PlannedTripResultsWithMapFragment fragment = new PlannedTripResultsWithMapFragment();
    Bundle args = new Bundle();
    args.putParcelable(EXTRA_ITINERARY, itinerary);
    args.putParcelableArrayList(EXTRA_BUS, busList);
    args.putParcelableArrayList(EXTRA_WALK, walkList);
    args.putParcelableArrayList(EXTRA_PATH, pathList);
    args.putString(EXTRA_STARTPOINTNAME, startPointName);
    args.putString(EXTRA_DESTINATIONNAME, destinationName);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      this.itinerary = getArguments().getParcelable(EXTRA_ITINERARY);
      this.busList = getArguments().getParcelableArrayList(EXTRA_BUS);
      this.walkList = getArguments().getParcelableArrayList(EXTRA_WALK);
      this.pathList = getArguments().getParcelableArrayList(EXTRA_PATH);
      this.startPointName = getArguments().getString(EXTRA_STARTPOINTNAME);
      this.destinationName = getArguments().getString(EXTRA_DESTINATIONNAME);
    }

    if (itinerary != null) {
      latLngBounds = getLatLngBounds();
      latLngList = new ArrayList<>();
      for (int i = 0; i < pathList.size(); i++) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        Path path = pathList.get(i);
        for (int j = 0; j < path.getShapes().size(); j++) {
          latLngs.add(new LatLng(
            path.getShapes().get(j).getShapeLat(),
            path.getShapes().get(j).getShapeLon()
          ));
        }
        latLngList.add(latLngs);
      }
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_planned_trips_result_with_map, container, false);
    unbinder = ButterKnife.bind(this, view);

    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    toolbar.setTitleTextColor(Color.WHITE);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setTitle("Direction");

    setHasOptionsMenu(true);

    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
      .findFragmentById(R.id.map_fragment_planned_trips_result_with_map);
    mapFragment.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        for (int i = 0; i < pathList.size(); i++) {
          PolylineOptions polylineOptions = new PolylineOptions();
          polylineOptions.width(25).geodesic(true);
          String routeColor = "#" + busList.get(i).getServices().get(0).getRoute().getRouteColor();
          polylineOptions.color(Color.parseColor(routeColor));
          polylineOptions.addAll(latLngList.get(i));

          Polyline polyline = map.addPolyline(polylineOptions);
          polyline.setStartCap(new RoundCap());
          polyline.setEndCap(new RoundCap());
          polyline.setJointType(JointType.ROUND);
        }

        double midLat = (latLngBounds.southwest.latitude + latLngBounds.northeast.latitude) / 2;
        double midLon = (latLngBounds.southwest.longitude + latLngBounds.northeast.longitude) / 2;
        LatLng midPoint = new LatLng(midLat, midLon);

        Log.d("LAT LNG : ", latLngBounds.getCenter().toString() + "Bounds: " + latLngBounds.toString());

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(midPoint, 15.0f),
          500,
          null
        );

        map.setPadding(0, 0, 0, tripInfoSubLayout.getHeight());
      }
    });

    int totalWalkMin = 0;
    for (Leg walk : walkList) {
      String timeInterval = TimeFormatter.getTimeInterval(
        getContext(),
        walk.getWalk().getBegin().getTime(),
        walk.getWalk().getEnd().getTime(),
        "plain"
      );

      totalWalkMin += Integer.valueOf(timeInterval);
    }
    String walkMin = "Walk " + String.valueOf(totalWalkMin) + "min";
    walkTimeTextView.setText(walkMin);

    String travelTime = itinerary.getTravelTime() + "min";
    String timeInterval = TimeFormatter.getTimeInterval(
      getContext(),
      itinerary.getStartTime(),
      itinerary.getEndTime(),
      "24hr"
    );
    travelTimeTextView.setText(travelTime);
    timeIntervalTextView.setText(timeInterval);

    setBusInfoRecyclerView();
    setTripInfoRecyclerView();

    bottomSheet.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        bottomSheet.getViewTreeObserver().removeOnPreDrawListener(this);
        Log.d("TAG TAG", String.valueOf(tripInfoSubLayout.getHeight()));
        BottomSheetBehavior bottomSheetBehavior =
          BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(tripInfoSubLayout.getHeight());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        return true;
      }
    });

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.home_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_home:
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  private void setBusInfoRecyclerView() {
    if (busList.size() != 0) {
      busInfoRecyclerView.setHasFixedSize(true);
      LayoutManager layoutManager = new LinearLayoutManager(
        getContext(), LinearLayoutManager.HORIZONTAL, false
      );
      busInfoRecyclerView.setLayoutManager(layoutManager);
      Adapter adapter = new BusInfoInBottomSheetAdapter(getContext(), busList);
      busInfoRecyclerView.setAdapter(adapter);
    }
  }

  private void setTripInfoRecyclerView() {
    if (itinerary.getLegs().size() != 0) {
      tripInfoRecyclerView.setHasFixedSize(true);
      LayoutManager layoutManager = new LinearLayoutManager(getContext());
      tripInfoRecyclerView.setLayoutManager(layoutManager);
      Adapter adapter = new TripInfoInBottomSheetAdapter(
        getContext(),
        itinerary,
        startPointName,
        destinationName);
      tripInfoRecyclerView.setAdapter(adapter);
    }
  }

  private LatLngBounds getLatLngBounds() {
    LatLng startPointLatLng;
    LatLng endPointLatLng;
    Leg startPoint = itinerary.getLegs().get(0);
    Leg endPoint = itinerary.getLegs().get(itinerary.getLegs().size() - 1);
    if (startPoint.getType().contentEquals(
      getContext().getResources().getString(R.string.type_walk))) {
      startPointLatLng = new LatLng(
        startPoint.getWalk().getBegin().getLat(),
        startPoint.getWalk().getBegin().getLon()
      );
    } else {
      startPointLatLng = new LatLng(
        startPoint.getServices().get(0).getBegin().getLat(),
        startPoint.getServices().get(0).getBegin().getLon()
      );
    }

    if (endPoint.getType().contentEquals(
      getContext().getResources().getString(R.string.type_walk))) {
      endPointLatLng = new LatLng(
        endPoint.getWalk().getEnd().getLat(),
        endPoint.getWalk().getEnd().getLon()
      );
    } else {
      endPointLatLng = new LatLng(
        endPoint.getServices().get(0).getEnd().getLat(),
        endPoint.getServices().get(0).getEnd().getLon()
      );
    }

    if (startPointLatLng.latitude > endPointLatLng.latitude) {
      return new LatLngBounds(endPointLatLng, startPointLatLng);
    } else if (startPointLatLng.latitude == endPointLatLng.latitude) {
      if (startPointLatLng.longitude > endPointLatLng.longitude) {
        return new LatLngBounds(endPointLatLng, startPointLatLng);
      }
    }

    return new LatLngBounds(startPointLatLng, endPointLatLng);
  }
}
