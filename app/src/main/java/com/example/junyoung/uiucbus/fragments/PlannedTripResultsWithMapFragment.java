package com.example.junyoung.uiucbus.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.example.junyoung.uiucbus.Constants;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.TimeFormatter;
import com.example.junyoung.uiucbus.adapters.BusInfoInBottomSheetAdapter;
import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.endpoints.ShapeEndpoints;
import com.example.junyoung.uiucbus.httpclient.pojos.Itinerary;
import com.example.junyoung.uiucbus.httpclient.pojos.Leg;
import com.example.junyoung.uiucbus.httpclient.pojos.Path;
import com.example.junyoung.uiucbus.httpclient.pojos.Service;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class PlannedTripResultsWithMapFragment extends Fragment {
  public static final String EXTRA_ITINERARY =
    "com.example.junyoung.uiucbus.fragments.EXTRA_ITINERARY";

  private MapView mapView;
  private GoogleMap map;
  private Itinerary itinerary;
  private int toolbarBottom;
  private int bottomSheetBottom;
  private ArrayList<Leg> busList = null;
  private ArrayList<Path> pathList = null;

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

  public static PlannedTripResultsWithMapFragment newInstance(Itinerary itinerary) {
    PlannedTripResultsWithMapFragment fragment = new PlannedTripResultsWithMapFragment();
    Bundle args = new Bundle();
    args.putParcelable(EXTRA_ITINERARY, itinerary);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      this.itinerary = getArguments().getParcelable(EXTRA_ITINERARY);
    }

    if (itinerary != null) {
      setBusList();
      getPathBetweenStops();
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_planned_trips_result_with_map, container, false);
    ButterKnife.bind(this, view);

    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    toolbar.setTitleTextColor(Color.WHITE);

    mapView = (MapView) view.findViewById(R.id.mapview_planned_trips_result_with_map);
    mapView.onCreate(savedInstanceState);
    mapView.onResume();

    try {
      MapsInitializer.initialize(getActivity().getApplicationContext());
    } catch (Exception e) {
      e.printStackTrace();
    }

    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        for (int i = 0; i < pathList.size(); i++) {
          PolylineOptions polylineOptions = new PolylineOptions();
          polylineOptions.width(25).geodesic(true);
          Path path = pathList.get(i);
          String routeColor = "#" + busList.get(i).getServices().get(0).getRoute().getRouteColor();
          polylineOptions.color(Color.parseColor(routeColor));
          for (int j = 0; j < path.getShapes().size(); j++) {
            polylineOptions.add(new LatLng(
              path.getShapes().get(j).getShapeLat(),
              path.getShapes().get(j).getShapeLon()
            ));
          }

          Polyline polyline = map.addPolyline(polylineOptions);
          polyline.setStartCap(new RoundCap());
          polyline.setEndCap(new RoundCap());
          polyline.setJointType(JointType.ROUND);
        }

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
          new LatLng(
            pathList.get(0).getShapes().get(0).getShapeLat(),
            pathList.get(0).getShapes().get(0).getShapeLon()
            ), 16.0f),
          500,
          null
        );
      }
    });

    String travelTime = itinerary.getTravelTime() + "min";
    String timeInterval = TimeFormatter.getTimeInterval(
      getContext(),
      itinerary.getStartTime(),
      itinerary.getEndTime()
    );
    travelTimeTextView.setText(travelTime);
    timeIntervalTextView.setText(timeInterval);

    setBusInfoRecyclerView();

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

  private void setBusList() {
    busList = new ArrayList<>();
    for (Leg leg : itinerary.getLegs()) {
      if (leg.getType().contentEquals(getResources().getString(R.string.type_service))) {
        busList.add(leg);
      }
    }
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

  private void getPathBetweenStops() {
    ShapeEndpoints shapeService =
      RetrofitBuilder.getRetrofitandRxJavaInstance().create(ShapeEndpoints.class);
    ArrayList<Single<Path>> pathSources = new ArrayList<>();
    for (Leg busService : busList) {
      Service service = busService.getServices().get(0);
      Single<Path> pathSource = shapeService.getShapeBetweenStops(
        Constants.API_KEY,
        service.getBegin().getStopId(),
        service.getEnd().getStopId(),
        service.getTrip().getShapeId()
      );
      pathSources.add(pathSource.subscribeOn(Schedulers.io()));
    }

    Single.zip(pathSources, new Function<Object[], ArrayList<Path>>() {
      @Override
      public ArrayList<Path> apply(Object[] objects) throws Exception {
        ArrayList<Path> pathList = new ArrayList<>();
        for (Object obj : objects) {
          pathList.add((Path) obj);
          Log.d("SIZE OF LIST: ", String.valueOf(((Path) obj).getShapes().size()));
        }
        return pathList;
      }
    })
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new DisposableSingleObserver<ArrayList<Path>>() {
        @Override
        public void onSuccess(ArrayList<Path> paths) {
          if (paths != null) {
            pathList = paths;
          }
        }

        @Override
        public void onError(Throwable e) {

        }
      });

  }
}
