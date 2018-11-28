package com.example.junyoung.culivebus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;

import com.example.junyoung.culivebus.fragment.DirectionSearchviewFragment;
import com.example.junyoung.culivebus.fragment.GoogleSearchFragment;
import com.example.junyoung.culivebus.fragment.NoInternetConnectionFragment;
import com.example.junyoung.culivebus.fragment.PlannedTripResultsWithMapFragment;
import com.example.junyoung.culivebus.fragment.PlannedTripsResultFragment;
import com.example.junyoung.culivebus.fragment.RecentDirectionFragment;
import com.example.junyoung.culivebus.httpclient.RetrofitBuilder;
import com.example.junyoung.culivebus.httpclient.services.ShapeService;
import com.example.junyoung.culivebus.httpclient.pojos.Itinerary;
import com.example.junyoung.culivebus.httpclient.pojos.Leg;
import com.example.junyoung.culivebus.httpclient.pojos.Path;
import com.example.junyoung.culivebus.httpclient.pojos.Service;
import com.example.junyoung.culivebus.room.entity.RouteInfo;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class GetDeparturesActivity extends AppCompatActivity
  implements DirectionSearchviewFragment.onEditTextClickListener,
  GoogleSearchFragment.OnActivityResultListener,
  DirectionSearchviewFragment.onDataRetrievedListener,
  PlannedTripsResultFragment.PlannedTripResultClickListener,
  RecentDirectionFragment.OnRecentDirectionClickedkListener,
  OnInternetConnectedListener {
  private static final String TAG = "GetDeparturesActivity";

  private boolean isTopEditText;
  private String startPointName;
  private String destinationName;

  private ConnectivityManager mConnectivityManager;

  private GoogleSearchFragment googleSearchFragment;
  private RecentDirectionFragment recentDirectionFragment;
  private DirectionSearchviewFragment searchviewFragment;
  private PlannedTripsResultFragment plannedTripsResultFragment;
  private NoInternetConnectionFragment noInternetConnectionFragment;
  private PlannedTripResultsWithMapFragment plannedTripResultsWithMapFragment;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    setContentView(R.layout.activity_get_departures);
    ButterKnife.bind(this);
    if (savedInstanceState == null) {
      searchviewFragment = new DirectionSearchviewFragment();
      recentDirectionFragment = new RecentDirectionFragment();
      recentDirectionFragment.setExitTransition(new Fade());
    }

    if (findViewById(R.id.framelayout_toolbar_get_departures) != null &&
      findViewById(R.id.framelayout_before_starting_google_search) != null) {
      getSupportFragmentManager().beginTransaction()
        .add(R.id.framelayout_toolbar_get_departures, searchviewFragment,
          DirectionSearchviewFragment.class.getSimpleName())
        .add(R.id.framelayout_before_starting_google_search, recentDirectionFragment)
        .commit();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume has called.");
  }

  @Override
  public void onInternetConnected(boolean isConnected, boolean shouldHideFragment) {
    if (!isConnected) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      if (findViewById(R.id.framelayout_toolbar_get_departures) != null) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(
          R.id.framelayout_toolbar_get_departures);
        if (fragment != null && shouldHideFragment) {
          transaction.hide(fragment);
        }
      }
      if (findViewById(R.id.framelayout_before_starting_google_search) != null) {
        noInternetConnectionFragment = new NoInternetConnectionFragment();
        if (getSupportFragmentManager().findFragmentById(
          R.id.framelayout_before_starting_google_search) != null) {
          transaction.replace(R.id.framelayout_before_starting_google_search,
            noInternetConnectionFragment);
        } else {
          transaction.add(R.id.framelayout_before_starting_google_search,
            noInternetConnectionFragment);
        }
      }
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onEditTextClick(boolean isTopEditText, String hint) {
    this.isTopEditText = isTopEditText;
    recentDirectionFragment.setExitTransition(null);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.hide(searchviewFragment);
    if (findViewById(R.id.framelayout_before_starting_google_search) != null) {
      googleSearchFragment = GoogleSearchFragment.newInstance(hint);
      if (getSupportFragmentManager().findFragmentById(
        R.id.framelayout_before_starting_google_search) != null) {
        transaction.replace(R.id.framelayout_before_starting_google_search, googleSearchFragment);
      } else {
        transaction.add(R.id.framelayout_before_starting_google_search, googleSearchFragment);
      }
    }
    transaction.addToBackStack(null);
    transaction.commit();
  }

  @Override
  public void onDataRetrieved() {
    if (plannedTripsResultFragment == null) {
      plannedTripsResultFragment = new PlannedTripsResultFragment();
    }
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.framelayout_before_starting_google_search, plannedTripsResultFragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }

  @Override
  public void onActivityResultExecuted(String placeName, LatLng placeLatLng, String hint) {
    Log.d(TAG, "onActivityResultExecuted is called");
    if (hint.contentEquals(getResources().getString(R.string.edittext_enter_starting_point_hint))) {
      startPointName = placeName;
    } else {
      destinationName = placeName;
    }
    searchviewFragment.updatePlaceName(placeName, isTopEditText);
    searchviewFragment.updatePlaceLatLng(placeLatLng, isTopEditText);
    FragmentManager manager = getSupportFragmentManager();
    manager.popBackStack();
    FragmentTransaction transaction = manager.beginTransaction();
    transaction.remove(googleSearchFragment);
    transaction.show(searchviewFragment);
    transaction.commit();
  }

  @Override
  public void onPlannedTripResultClicked() {
    if (plannedTripResultsWithMapFragment == null) {
      plannedTripResultsWithMapFragment = new PlannedTripResultsWithMapFragment();
    }
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    // transaction.detach(plannedTripsResultFragment);
    transaction.detach(searchviewFragment);
    transaction.replace(
      R.id.framelayout_before_starting_google_search,
      plannedTripResultsWithMapFragment
    );
    transaction.addToBackStack(null);
    transaction.commit();
  }

  @Override
  public void onRecentDirectionClick(RouteInfo directionInfo) {
    if (recentDirectionFragment != null) {
      recentDirectionFragment.setExitTransition(new Fade());
    }

    searchviewFragment = (DirectionSearchviewFragment)
      getSupportFragmentManager().findFragmentByTag(DirectionSearchviewFragment.class
        .getSimpleName());
    if (searchviewFragment != null) {
      searchviewFragment.updateDirectionInfo(directionInfo);
    }
  }

  private void getPathBetweenStops(final Itinerary itinerary,
                                   final List<Leg> busList,
                                   final List<Leg> walkList) {
    ShapeService shapeService =
      RetrofitBuilder.getRetrofitandRxJavaInstance().create(ShapeService.class);
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
    }).subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new DisposableSingleObserver<ArrayList<Path>>() {
        @Override
        public void onSuccess(ArrayList<Path> paths) {
          if (paths != null) {
            ArrayList<Path> pathList = paths;
          }
        }

        @Override
        public void onError(Throwable e) {
          e.printStackTrace();
        }
      });
  }
}
