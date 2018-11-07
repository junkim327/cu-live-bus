package com.example.junyoung.uiucbus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.uiucbus.room.entity.RouteInfo;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedDirectionInfoViewModel;
import com.example.junyoung.uiucbus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.httpclient.pojos.Shape;
import com.example.junyoung.uiucbus.ui.viewmodel.BusPathViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.Response;
import com.example.junyoung.uiucbus.ui.viewmodel.SharedTripViewModel;
import com.example.junyoung.uiucbus.util.TimeFormatter;
import com.example.junyoung.uiucbus.adapter.BusInfoInBottomSheetAdapter;
import com.example.junyoung.uiucbus.adapter.TripInfoInBottomSheetAdapter;
import com.example.junyoung.uiucbus.httpclient.pojos.Itinerary;
import com.example.junyoung.uiucbus.httpclient.pojos.Leg;
import com.example.junyoung.uiucbus.httpclient.pojos.Path;
import com.example.junyoung.uiucbus.util.UtilConnection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PlannedTripResultsWithMapFragment extends Fragment implements OnMapReadyCallback {
  private static final int POLYLINE_STROKE_WIDTH_PX = 20;
  private static final String TAG = PlannedTripResultsWithMapFragment.class.getSimpleName();
  private int toolbarBottom;
  private int bottomSheetBottom;
  private boolean mIsInternetConnected = true;
  private String startPointName;
  private String destinationName;
  private List<Leg> mBusList = new ArrayList<>();
  private List<Leg> mWalkList = new ArrayList<>();

  private RouteInfo mDirectionInfo;
  private GoogleMap mMap;
  private Unbinder unbinder;
  private ConnectivityManager mConnectivityManager;
  private TripInfoInBottomSheetAdapter mTripAdapter;
  private BusInfoInBottomSheetAdapter mBusInfoAdapter;
  private BusPathViewModel mBusPathViewModel;
  private SharedTripViewModel mSharedTripViewModel;
  private SharedDirectionInfoViewModel mSharedDirectionInfoViewModel;

  private OnInternetConnectedListener mInternetConnectedCallback;

  @BindView(R.id.toolbar_planned_trips_result_with_map)
  Toolbar toolbar;
  @BindView(R.id.textview_walk_time_bottom_sheet_planned_trip_results)
  TextView mWalkTimeTextView;
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
                                                              ArrayList<Path> pathList,
                                                              String startPointName,
                                                              String destinationName) {
    PlannedTripResultsWithMapFragment fragment = new PlannedTripResultsWithMapFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "onAttach has called");

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

    Log.d(TAG, "onCreate has called.");

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView has called");
    if (mConnectivityManager != null) {
      mIsInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, false);
    }

    View view = null;
    if (mIsInternetConnected) {
      view = inflater.inflate(R.layout.fragment_planned_trips_result_with_map, container, false);
      unbinder = ButterKnife.bind(this, view);

      setToolbar();
      setBusInfoRecyclerView();
      setTripInfoRecyclerView();
      setBottomSheetBehavior();
      loadMapFragment();
    }

    return view;
  }

  private void setToolbar() {
    setHasOptionsMenu(true);

    if (getActivity() != null) {
      ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }
    toolbar.setTitleTextColor(Color.WHITE);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle("Direction");
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }
  }

  private void setBusInfoRecyclerView() {
    busInfoRecyclerView.setHasFixedSize(true);

    LayoutManager layoutManager = new LinearLayoutManager(
      getContext(), LinearLayoutManager.HORIZONTAL, false
    );
    busInfoRecyclerView.setLayoutManager(layoutManager);

    mBusInfoAdapter = new BusInfoInBottomSheetAdapter(getContext());
    busInfoRecyclerView.setAdapter(mBusInfoAdapter);
  }

  private void setTripInfoRecyclerView() {
    tripInfoRecyclerView.setHasFixedSize(true);

    LayoutManager layoutManager = new LinearLayoutManager(getContext());
    tripInfoRecyclerView.setLayoutManager(layoutManager);

    mTripAdapter = new TripInfoInBottomSheetAdapter(getContext());
    tripInfoRecyclerView.setAdapter(mTripAdapter);

    // tripInfoRecyclerView.setNestedScrollingEnabled(false);
  }

  private void setBottomSheetBehavior() {
    BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    bottomSheetBehavior.setPeekHeight(200);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
    bottomSheetBehavior.setFitToContents(false);
  }

  private void loadMapFragment() {
    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
      .findFragmentById(R.id.map_fragment_planned_trips_result_with_map);
    mapFragment.getMapAsync(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    mSharedDirectionInfoViewModel = ViewModelProviders.of(getActivity())
      .get(SharedDirectionInfoViewModel.class);
    mDirectionInfo = mSharedDirectionInfoViewModel.getRouteInfo().getValue();
    mTripAdapter.updateDirectionInfo(mDirectionInfo);
    addMarkers();

    mBusPathViewModel = ViewModelProviders.of(this).get(BusPathViewModel.class);
    mBusPathViewModel.getBusPathList().observe(this, pathResponse -> {
      if (pathResponse != null) {
        processResponse(pathResponse);
      }
    });

    mSharedTripViewModel = ViewModelProviders.of(getActivity()).get(SharedTripViewModel.class);
    mSharedTripViewModel.getItinerary().observe(this, itinerary -> {
      if (itinerary != null) {
        mTripAdapter.updateItinerary(itinerary);
        updateBottomSheetInfo(itinerary);
        sortBusAndWalkList(itinerary);
        setTotalWalkMin();
        mBusInfoAdapter.updateBusList(mBusList);
        mBusPathViewModel.requestPathList(mBusList);
      }
    });
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  private void processResponse(Response<List<Path>> response) {
    switch (response.mStatus) {
      case LOADING:
        break;
      case SUCCESS:
        if (response.mData != null) {
          drawPolyline(response.mData);
        }
        break;
      case ERROR:
        break;
    }
  }

  private void addMarkers() {
    if (mDirectionInfo != null && mMap != null) {
      // Add origin marker
      Log.d(TAG, "Add origin marker");
      mMap.addMarker(new MarkerOptions()
        .position(
          new LatLng(
            Double.valueOf(mDirectionInfo.getOriginLat()),
            Double.valueOf(mDirectionInfo.getOriginLon())
          )
        )
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.starting_point_icon))
      );

      // Add destination marker
      mMap.addMarker(new MarkerOptions()
        .position(
          new LatLng(
          Double.valueOf(mDirectionInfo.getDestinationLat()),
          Double.valueOf(mDirectionInfo.getDestinationLon())
          )
        ).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_icon))
      );
    }
  }

  private void drawPolyline(List<Path> pathList) {
    if (pathList.size() > 0) {
      LatLngBounds.Builder builder = LatLngBounds.builder();
      PolylineOptions polylineOptions = new PolylineOptions();
      polylineOptions.width(POLYLINE_STROKE_WIDTH_PX).geodesic(true);

      // Add walk polyline
      if (mDirectionInfo != null) {
        Shape firstBusStop = pathList.get(0).getShapes().get(0);
        mMap.addPolyline(new PolylineOptions()
          .add(
            new LatLng(
              Double.valueOf(mDirectionInfo.getOriginLat()),
              Double.valueOf(mDirectionInfo.getOriginLon())
            ),
            new LatLng(
              firstBusStop.getShapeLat(),
              firstBusStop.getShapeLon()
            )
          ).width(POLYLINE_STROKE_WIDTH_PX).geodesic(true)
          .pattern(Arrays.<PatternItem>asList(new Dot()))
        );
      }

      for (int i = 0; i < pathList.size(); i++) {
        polylineOptions.color(Color.parseColor(getResources().getString(R.string.hex_color,
          mBusList.get(i).getServices().get(0).getRoute().getRouteColor())));
        Path path = pathList.get(i);
        for (Shape shape : path.getShapes()) {
          LatLng point = new LatLng(shape.getShapeLat(), shape.getShapeLon());
          builder.include(point);
          polylineOptions.add(point);
        }
        Polyline polyline = mMap.addPolyline(polylineOptions);
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setJointType(JointType.ROUND);
      }

      mMap.setOnMapLoadedCallback(() -> {
        mMap.setLatLngBoundsForCameraTarget(builder.build());
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
      });
    }
  }

  private void updateBottomSheetInfo(Itinerary itinerary) {
    travelTimeTextView.setText(getResources().getString(
      R.string.travel_time,
      itinerary.getTravelTime())
    );

    timeIntervalTextView.setText(TimeFormatter.getTimeInterval(
      getContext(),
      itinerary.getStartTime(),
      itinerary.getEndTime(),
      getResources().getString(R.string.format_24hr))
    );
  }

  private void sortBusAndWalkList(Itinerary itinerary) {
    for (Leg leg : itinerary.getLegs()) {
      if (leg.getType().startsWith("W")) {
        mWalkList.add(leg);
      } else {
        mBusList.add(leg);
      }
    }
  }

  private String getTotalWalkMin(List<Leg> walkList) {
    long totalWalkTimeInMilli = 0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
    dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
    SimpleDateFormat newFormat = new SimpleDateFormat("hh:mm a", Locale.US);
    newFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));

    for (Leg walk : walkList) {
      try {
        Date startTimeData = dateFormat.parse(walk.getWalk().getBegin().getTime());
        Date endTimeData = dateFormat.parse(walk.getWalk().getEnd().getTime());
        totalWalkTimeInMilli += (endTimeData.getTime() - startTimeData.getTime());
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    return String.valueOf(TimeUnit.MILLISECONDS.toMinutes(totalWalkTimeInMilli));
  }

  private void setTotalWalkMin() {
    if (mWalkList != null && mWalkList.size() > 0) {
      mWalkTimeTextView.setText(getResources().getString(
        R.string.walk_time,
        getTotalWalkMin(mWalkList))
      );
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.home_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        if (getFragmentManager() != null) {
          getFragmentManager().popBackStackImmediate();
        }
        return true;
      case R.id.action_home:
        NavUtils.navigateUpFromSameTask(getActivity());
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onStop() {
    super.onStop();

    mBusList.clear();
    mWalkList.clear();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    unbinder.unbind();
  }
}
