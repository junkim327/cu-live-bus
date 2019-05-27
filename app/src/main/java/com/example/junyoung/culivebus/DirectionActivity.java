package com.example.junyoung.culivebus;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.example.junyoung.culivebus.ui.common.DirectionNavigationController;
import com.example.junyoung.culivebus.httpclient.RetrofitBuilder;
import com.example.junyoung.culivebus.httpclient.services.ShapeService;
import com.example.junyoung.culivebus.vo.Itinerary;
import com.example.junyoung.culivebus.vo.Leg;
import com.example.junyoung.culivebus.httpclient.pojos.Path;
import com.example.junyoung.culivebus.vo.Service;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DirectionActivity extends AppCompatActivity implements HasSupportFragmentInjector,
  OnInternetConnectedListener {
  private static final String TAG = "DirectionActivity";

  @Inject
  DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
  @Inject
  DirectionNavigationController navigationController;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_direction);

    if (savedInstanceState == null) {
      navigationController.navigateToResult(false);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume has called.");
  }

  @Override
  public void onInternetConnected(boolean isConnected, boolean shouldHideFragment) {
    /*if (!isConnected) {
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
    }*/
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

  @Override
  public AndroidInjector<Fragment> supportFragmentInjector() {
    return dispatchingAndroidInjector;
  }
}
