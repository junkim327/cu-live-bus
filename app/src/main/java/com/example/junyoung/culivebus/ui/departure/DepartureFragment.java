package com.example.junyoung.culivebus.ui.departure;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.junyoung.culivebus.binding.FragmentDataBindingComponent;
import com.example.junyoung.culivebus.databinding.DepartureFragmentBinding;
import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.example.junyoung.culivebus.httpclient.pojos.Departure;
import com.example.junyoung.culivebus.ui.common.NavigationController;
import com.example.junyoung.culivebus.util.AutoClearedValue;
import com.example.junyoung.culivebus.util.EventObserver;
import com.example.junyoung.culivebus.vo.Function;
import com.example.junyoung.culivebus.vo.Resource;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.vo.SortedDeparture;
import com.example.junyoung.culivebus.ui.common.SharedDepartureViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;

public class DepartureFragment extends DaggerFragment implements OnMapLoadedCallback {
  private static final String EXTRA_STOP_ID = "STOP_ID";
  private static final String EXTRA_STOP_NAME = "STOP_NAME";
  private static final String EXTRA_STOP_CODE = "STOP_CODE";
  private static final String EXTRA_LATITUDE = "LATITUDE";
  private static final String EXTRA_LONGITUDE = "LONGITUDE";
  private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

  @Inject
  ViewModelProvider.Factory viewModelFactory;
  @Inject
  NavigationController navigationController;

  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<DepartureFragmentBinding> binding;

  private MapView mapView;
  private Bundle mapViewBundle;
  private DepartureViewModel departureViewModel;
  private SharedDepartureViewModel sharedDepartureViewModel;
  private DepartureListAdapter adapter;

  public static DepartureFragment newInstance(String stopId, String stopName, String stopCode,
                                              double latitude, double longitude) {
    DepartureFragment fragment = new DepartureFragment();
    Bundle bundle = new Bundle();
    bundle.putString(EXTRA_STOP_ID, stopId);
    bundle.putString(EXTRA_STOP_NAME, stopName);
    bundle.putString(EXTRA_STOP_CODE, stopCode);
    bundle.putDouble(EXTRA_LATITUDE, latitude);
    bundle.putDouble(EXTRA_LONGITUDE, longitude);
    fragment.setArguments(bundle);

    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    Timber.d("onCreate");
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    Timber.d("onCreateView");
    departureViewModel = ViewModelProviders.of(this, viewModelFactory)
      .get(DepartureViewModel.class);
    DepartureFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.fragment_departure, container, false, dataBindingComponent);

    dataBinding.setViewModel(departureViewModel);
    dataBinding.setLifecycleOwner(getViewLifecycleOwner());

    adapter = new DepartureListAdapter(getViewLifecycleOwner(), departureViewModel);
    dataBinding.busDepartureList.setAdapter(adapter);

    mapView = dataBinding.toolbarLayout.mapView;
    mapView.onCreate(mapViewBundle);
    initializeMap();

    binding = new AutoClearedValue<>(this, dataBinding);

    return dataBinding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    Timber.d("onViewCreated");
    sharedDepartureViewModel = ViewModelProviders.of(getActivity(), viewModelFactory)
      .get(SharedDepartureViewModel.class);

    initToolbar();
    initAppBarLayout();

    departureViewModel.getDepartures().observe(getViewLifecycleOwner(),
      new Observer<Resource<List<SortedDeparture>>>() {
      @Override
      public void onChanged(Resource<List<SortedDeparture>> resource) {
        if (resource != null) {
          Timber.d("Status: %s", resource.status);
          processResource(resource);
        }
      }
    });

    departureViewModel.getToastMessage().observe(getViewLifecycleOwner(), new EventObserver<>(
      new Function<String>() {
        @Override
        public void invoke(String toastMessage) {
          if (toastMessage != null) {
            displayToast(toastMessage, getActivity());
          }
        }
      }
    ));

    departureViewModel.getClickedDeparture().observe(getViewLifecycleOwner(), new EventObserver<>(
      new Function<Departure>() {
        @Override
        public void invoke(Departure departure) {
          if (departure != null) {
            navigationController.navigateToRouteActivity(departure);
          }
        }
      }
    ));

    departureViewModel.isResumed().observe(getViewLifecycleOwner(), new EventObserver<>(
      new Function<Boolean>() {
        @Override
        public void invoke(Boolean resumed) {
          if (resumed) {
            departureViewModel.initTimer();
          } else {
            departureViewModel.cancelTimer();
          }
        }
      }
    ));
  }

  @Override
  public void onStart() {
    super.onStart();
    Timber.d("onStart");
//    Bundle bundle = Objects.requireNonNull(getArguments());
    Bundle bundle = getArguments();
    if (bundle != null) {
      departureViewModel.setStopPoint(
        new StopPoint(
          bundle.getString(EXTRA_STOP_ID),
          bundle.getString(EXTRA_STOP_CODE),
          bundle.getString(EXTRA_STOP_NAME),
          bundle.getDouble(EXTRA_LATITUDE),
          bundle.getDouble(EXTRA_LONGITUDE)
        )
      );
    }

    departureViewModel.setResumed(true);
  }

  private void processResource(Resource<List<SortedDeparture>> resource) {
    switch (resource.status) {
      case LOADING:
        break;
      case SUCCESS:
        adapter.replace(resource.data);
        for (SortedDeparture sortedDeparture : resource.data) {
          Timber.d("HeadSign : %s", sortedDeparture.getHeadSign());
        }
        break;
      case ERROR:
        Timber.e(resource.message);
        if (resource.message.contentEquals("empty")) {
          adapter.replace(null);
        } else {
          createSnackbar();
        }
        departureViewModel.cancelTimer();
        break;
    }
  }

  private void createSnackbar() {
    Snackbar.make(
      binding.get().parentLayout,
      R.string.snack_bar_network_error_message,
      Snackbar.LENGTH_INDEFINITE
    ).setAction("RETRY", new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        departureViewModel.initTimer();
      }
    }).show();
  }

  @Override
  public void onResume() {
    Timber.d("onResume");
    super.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();

    departureViewModel.setResumed(false);
    adapter.replace(null);
    Timber.d("onPause has finished");
  }

  @Override
  public void onStop() {
    Timber.d("onStop");
    super.onStop();
  }

  @Override
  public void onMapLoaded() {
    Timber.d("onMapLoaded has been called");
    departureViewModel.setIsMapLoaded(true);
  }

  private void initializeMap() {
    mapView.getMapAsync(googleMap -> googleMap.setOnMapLoadedCallback(this));
  }

  private void initToolbar() {
    Toolbar toolbar = binding.get().toolbar;
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);

    toolbar.setNavigationOnClickListener(view -> {
      FragmentManager fm = getFragmentManager();
      if (fm != null && fm.getBackStackEntryCount() > 0) {
        fm.popBackStack();
      }
    });
  }

  private void initAppBarLayout() {
    disableDraggingBehavior();

    CollapsingToolbarLayout collapsingToolbarLayout = binding.get().collapsingToolbarLayout;

    binding.get().appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      boolean isShow = true;
      int scrollRange = -1;

      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
          isShow = true;
          collapsingToolbarLayout.setTitle(binding.get().toolbarLayout.busStopName.getText());
        } else if (isShow) {
          isShow = false;
          collapsingToolbarLayout.setTitle(" ");
        }
      }
    });
  }

  private void disableDraggingBehavior() {
    AppBarLayout appBarLayout = binding.get().appbarLayout;
    if (appBarLayout.getLayoutParams() != null) {
      CoordinatorLayout.LayoutParams layoutParams =
        (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
      AppBarLayout.Behavior appBarLayoutBehavior = new AppBarLayout.Behavior();
      appBarLayoutBehavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
        @Override
        public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
          return false;
        }
      });
      layoutParams.setBehavior(appBarLayoutBehavior);
    }
  }

  private void displayToast(CharSequence message, Context context) {
    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

    // Change toast background color
    toast.getView().getBackground().setColorFilter(
      ResourcesCompat.getColor(getResources(), R.color.toast_background_color, null),
      PorterDuff.Mode.SRC_IN
    );

    // Change toast text color
    TextView text = toast.getView().findViewById(android.R.id.message);
    text.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));

    toast.show();
  }
}
