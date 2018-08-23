package com.example.junyoung.uiucbus;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.endpoints.ShapeEndpoints;
import com.example.junyoung.uiucbus.httpclient.endpoints.VehicleEndpoints;
import com.example.junyoung.uiucbus.httpclient.pojos.Location;
import com.example.junyoung.uiucbus.httpclient.pojos.Path;
import com.example.junyoung.uiucbus.httpclient.pojos.PathAndVehicle;
import com.example.junyoung.uiucbus.httpclient.pojos.Shape;
import com.example.junyoung.uiucbus.httpclient.pojos.VehicleData;
import com.google.android.gms.common.data.DataBufferObserver;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import io.reactivex.Observable;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusPathOnMapActivity extends AppCompatActivity implements
  OnMapReadyCallback {
  private static final String TAG = "BusPathOnMapActivity";

  private String shapeId;
  private String busName;
  private String busVehicleId;
  private Location busLocation;
  private ArrayList<Shape> shapeList;

  private GoogleMap map;

  @BindView(R.id.progressbar_bus_path)
  ProgressBar busPathProgressBar;
  @BindView(R.id.toolbar_bus_path_on_map)
  Toolbar busPathOnMapToolbar;
  @BindView(R.id.constraint_layout_bus_path)
  ConstraintLayout busPathConstraintLayout;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bus_path_on_map);
    ButterKnife.bind(this);

    Intent intent = getIntent();

    shapeId = intent.getStringExtra(BusRoutesActivity.EXTRA_SHAPEID);
    busName = intent.getStringExtra(BusDeparturesActivity.EXTRA_BUSNAME);
    busVehicleId = intent.getStringExtra(BusDeparturesActivity.EXTRA_VEHICLEID);

    setSupportActionBar(busPathOnMapToolbar);

    setToolBar();

    Observable<Path> pathObservable = RetrofitBuilder.getRetrofitandRxJavaInstance()
      .create(ShapeEndpoints.class)
      .getShapeObservable(Constants.API_KEY, shapeId)
      .subscribeOn(Schedulers.newThread())
      .observeOn(AndroidSchedulers.mainThread());

    Observable<VehicleData> vehicleObservable = RetrofitBuilder.getRetrofitandRxJavaInstance()
      .create(VehicleEndpoints.class)
      .getVehicleObservable(Constants.API_KEY, busVehicleId)
      .subscribeOn(Schedulers.newThread())
      .observeOn(AndroidSchedulers.mainThread());

    Observable<PathAndVehicle> combined = Observable.zip(
      pathObservable, vehicleObservable, new BiFunction<Path, VehicleData, PathAndVehicle>() {
        @Override
        public PathAndVehicle apply(Path path, VehicleData vehicleData) throws Exception {
          return new PathAndVehicle(path, vehicleData);
        }
      });

    combined.subscribe(new Subject<PathAndVehicle>() {
      @Override
      public boolean hasObservers() {
        return false;
      }

      @Override
      public boolean hasThrowable() {
        return false;
      }

      @Override
      public boolean hasComplete() {
        return false;
      }

      @Override
      public Throwable getThrowable() {
        return null;
      }

      @Override
      protected void subscribeActual(Observer<? super PathAndVehicle> observer) {

      }

      @Override
      public void onSubscribe(Disposable d) {

      }

      @Override
      public void onNext(PathAndVehicle pathAndVehicle) {
        shapeList = pathAndVehicle.getPath().getShapes();
        busLocation = pathAndVehicle.getVehicleData().getVehicles().get(0).getLocation();
      }

      @Override
      public void onError(Throwable e) {

      }

      @Override
      public void onComplete() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
          .findFragmentById(R.id.map_bus_path);
        mapFragment.getMapAsync(BusPathOnMapActivity.this);
      }
    });
  }

  private void setToolBar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setTitle(busName);
    busPathOnMapToolbar.setTitleTextColor(Color.WHITE);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    map = googleMap;

    createBusMarker();

    PolylineOptions polylineOptions = new PolylineOptions();
    for (int i = 0; i < shapeList.size(); i++) {
      Shape shape = shapeList.get(i);
      polylineOptions.add(new LatLng(shape.getShapeLat(), shape.getShapeLon()));
    }

    polylineOptions
      .width(20)
      .color(getResources().getColor(R.color.progressbar_color))
      .geodesic(true);

    Polyline polyline = map.addPolyline(polylineOptions);
    polyline.setStartCap(new RoundCap());
    polyline.setEndCap(new RoundCap());
    polyline.setJointType(JointType.ROUND);

    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
      new LatLng(busLocation.getLat(), busLocation.getLon()), 14.0f), 500, null
    );

    busPathProgressBar.setVisibility(View.GONE);
    busPathConstraintLayout.setVisibility(View.VISIBLE);
  }

  private void createBusMarker() {
    LatLng busPosition = new LatLng(busLocation.getLat(), busLocation.getLon());
    Marker marker = map.addMarker(new MarkerOptions()
      .position(busPosition)
      .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker))
    );
  }
}
