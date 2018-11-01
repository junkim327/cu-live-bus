package com.example.junyoung.uiucbus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junyoung.uiucbus.Constants;
import com.example.junyoung.uiucbus.MainActivity;
import com.example.junyoung.uiucbus.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.adapter.BusFavoriteDeparturesAdapter;
import com.example.junyoung.uiucbus.adapter.MainViewPagerAdapter;
import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.pojos.BusInfo;
import com.example.junyoung.uiucbus.httpclient.pojos.BusSchedules;
import com.example.junyoung.uiucbus.httpclient.pojos.DeparturesByStop;
import com.example.junyoung.uiucbus.httpclient.pojos.NotifyItemData;
import com.example.junyoung.uiucbus.httpclient.services.DepartureService;
import com.example.junyoung.uiucbus.ui.Injection;
import com.example.junyoung.uiucbus.ui.factory.UserSavedBusStopViewModelFactory;
import com.example.junyoung.uiucbus.ui.viewmodel.BusDeparturesViewModel;
import com.example.junyoung.uiucbus.ui.viewmodel.SavedBusStopViewModel;
import com.example.junyoung.uiucbus.util.UtilConnection;
import com.example.junyoung.uiucbus.util.UtilSort;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DashboardFragment extends Fragment {
  private static final String TAG = DashboardFragment.class.getSimpleName();

  private String mUid;
  private boolean isInternetConnected;
  private int mNumBanner = 0;
  private int mNumUserSavedBusStops;

  private BusSchedules busSchedules = new BusSchedules();
  private ArrayList<Integer> bannerIndex = new ArrayList<>();

  private Unbinder mUnbinder;
  private ConnectivityManager mConnectivityManager;
  private BusFavoriteDeparturesAdapter mAdapter;
  private BusDeparturesViewModel mBusDepartureViewModel;
  private SavedBusStopViewModel mViewModel;
  private UserSavedBusStopViewModelFactory mViewModelFactory;
  private OnInternetConnectedListener mInternetConnectedCallback;
  private OnSettingItemSelectedListener mSettingSelectedCallback;
  private OnBusStopsSearchviewClickedListener mBusStopsSearchviewClickedCallback;
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  @BindView(R.id.toolbar_dashboard)
  Toolbar mToolbar;
  @BindView(R.id.edittext_search_bus_stops_dashboard)
  EditText mSearchBusStopsEditText;
  @BindView(R.id.viewpager_dashboard)
  ViewPager mViewPager;
  @BindView(R.id.recyclerview_dashboard)
  RecyclerView mRecyclerview;
  @BindView(R.id.text_data_provider_dashboard)
  TextView mDataProviderText;
  @BindView(R.id.image_when_no_favorite_bus_stops_dashboard)
  ImageView mGuidanceImage;
  @BindView(R.id.text_guidance_message_one_dashboard)
  TextView mGuidanceTextOne;
  @BindView(R.id.text_guidance_message_two_dashboard)
  TextView mGuidanceTextTwo;

  public interface OnSettingItemSelectedListener {
    void onSettingItemSelected();
  }

  public interface OnBusStopsSearchviewClickedListener {
    void onBusStopsSearchviewClicked();
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
      mSettingSelectedCallback = (OnSettingItemSelectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnSettingItemSelectedListener.");
    }

    try {
      mBusStopsSearchviewClickedCallback = (OnBusStopsSearchviewClickedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnBusStopsSearchviewClickedListener.");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );

    mUid = sharedPref.getString(getString(R.string.saved_uid), null);
    // mNumUserSavedBusStops = sharedPref.getInt(getString(R.string.saved_bus_stops), 0);

    mViewModelFactory = Injection.provideUserSavedBusStopViewModelFactory(getContext());
    mViewModel = ViewModelProviders.of(this, mViewModelFactory)
      .get(SavedBusStopViewModel.class);
  }

  private void setToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      setHasOptionsMenu(true);
      actionBar.setDisplayShowTitleEnabled(false);
    }
  }

  private void setViewPager() {
    mViewPager.setClipToPadding(false);
    mViewPager.setPageMargin(24);
    mViewPager.setAdapter(new MainViewPagerAdapter(getContext()));
  }

  private void setRecyclerView() {
    mRecyclerview.setHasFixedSize(true);
    mRecyclerview.setNestedScrollingEnabled(false);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerview.setLayoutManager(layoutManager);
    ((SimpleItemAnimator) mRecyclerview.getItemAnimator())
      .setSupportsChangeAnimations(false);

    mAdapter = new BusFavoriteDeparturesAdapter(getContext(), MainActivity.TAG, new BusSchedules());
    mRecyclerview.setAdapter(mAdapter);
  }

  private void controlViewVisibility() {
    if (mNumUserSavedBusStops != 0) {
      mRecyclerview.setVisibility(VISIBLE);
      mDataProviderText.setVisibility(VISIBLE);
      mGuidanceImage.setVisibility(GONE);
      mGuidanceTextOne.setVisibility(GONE);
      mGuidanceTextTwo.setVisibility(GONE);

      setRecyclerView();
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    isInternetConnected = true;
    if (mConnectivityManager != null) {
      isInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, false);
    }

    View view = null;
    if (isInternetConnected) {
      view = inflater.inflate(R.layout.fragment_dashboard, container, false);
      mUnbinder = ButterKnife.bind(this, view);

      setToolbar();
      setViewPager();
    }

    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mBusDepartureViewModel = ViewModelProviders.of(this).get(BusDeparturesViewModel.class);
  }

  private void performGETBusDeparturesRequest(final ArrayList<String> savedStopIds,
                                              final ArrayList<String> savedStopCodes,
                                              final ArrayList<String> savedStopNames) {
    DepartureService service =
      RetrofitBuilder.getRetrofitandRxJavaInstance().create(DepartureService.class);
    final List<Observable<DeparturesByStop>> observableList = new ArrayList<>();
    for (String stopId : savedStopIds) {
      Observable<DeparturesByStop> observable =
        service.getDeparturesByStop(Constants.API_KEY, stopId);
      observableList.add(observable.subscribeOn(Schedulers.io()));
    }

    Observable<List<DeparturesByStop>> obs= Observable.zip(observableList, objects -> {
      List<DeparturesByStop> stopList = new ArrayList<>();
      for (Object obj : objects) {
        stopList.add((DeparturesByStop) obj);
      }
      Log.i(TAG, "Size of stop list : " + stopList.size());

      return stopList;
    });

    Observable.interval(10, TimeUnit.SECONDS).timeInterval()
      .flatMap(new Function<Timed<Long>, ObservableSource<List<DeparturesByStop>>>() {
        @Override
        public ObservableSource<List<DeparturesByStop>> apply(Timed<Long> longTimed) throws
          Exception {
          return Observable.zip(observableList, objects -> {
            List<DeparturesByStop> stopList = new ArrayList<>();
            for (Object obj : objects) {
              stopList.add((DeparturesByStop) obj);
            }
            Log.i(TAG, "Size of stop list : " + stopList.size());

            return stopList;
          });
        }
      }).observeOn(AndroidSchedulers.mainThread())
      .subscribe(new DisposableObserver<List<DeparturesByStop>>() {
        @Override
        public void onNext(List<DeparturesByStop> departuresByStops) {
          Log.d(TAG, "onNext has called with some items: " + departuresByStops.size());
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
      });

    Observable<Long> interval = Observable.interval(1, TimeUnit.SECONDS);

    Observable<List<DeparturesByStop>> everyMinute = obs.sample(interval);

    mDisposable.add(everyMinute
      .subscribeOn(Schedulers.computation())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeWith(new DisposableObserver<List<DeparturesByStop>>() {
        @Override
        public void onNext(List<DeparturesByStop> departuresByStops) {
          Log.d(TAG, "onNext has called");
          if (departuresByStops != null && departuresByStops.size() != 0) {
            /*
            if (mNumBanner != 0) {
              //busSchedules = mAdapter.getBusSchedules();
            }
            String busStopName;
            ArrayList<Integer> headerPosition = new ArrayList<>();
            int numStopIds = savedStopIds.size();
            for (int i = 0; i < numStopIds; i++) {
              if (mNumBanner < numStopIds) {
                mNumBanner++;
                busStopName = savedStopNames.get(i);
                busSchedules.addBusName(busStopName);
                headerPosition.add(busSchedules.getBusNameList().size() - 1);
                busSchedules.addBusStopInfo(new BusInfo(savedStopIds.get(i), savedStopCodes.get(i)));
                UtilSort.sortDeparturesByBus2(
                  busStopName,
                  busSchedules,
                  departuresByStops.get(i).getDepartures(),
                  bannerIndex
                );
                //mAdapter.updateBusSchedules(busSchedules, headerPosition);
              } else {
                NotifyItemData itemData = UtilSort.sortDeparturesByBus3(
                  busSchedules,
                  departuresByStops.get(i).getDepartures(),
                  bannerIndex.get(i));
                //mAdapter.updateBusData(itemData);
              }
            }
            //progressBar.setVisibility(GONE);
            */
          }
        }

        @Override
        public void onComplete() {

        }

        @Override
        public void onError(Throwable e) {
          e.printStackTrace();
        }
      }));
  }

  @OnClick(R.id.edittext_search_bus_stops_dashboard)
  public void startBusStopSearchFragment() {
    mBusStopsSearchviewClickedCallback.onBusStopsSearchviewClicked();
  }

  @Override
  public void onStart() {
    super.onStart();

    if (mUid != null) {
      mDisposable.add(mViewModel.loadAllBusStopsByUid(mUid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(userSavedBusStops -> {
            Log.d(TAG, "Load user saved bus stops successfully.");
            if (userSavedBusStops != null) {
              mNumUserSavedBusStops = userSavedBusStops.size();
              controlViewVisibility();

              ArrayList<String> busStopId = new ArrayList<>();
              ArrayList<String> busStopCode = new ArrayList<>();
              ArrayList<String> busStopName = new ArrayList<>();
              for (int i = 0; i < userSavedBusStops.size(); i++) {
                busStopId.add(userSavedBusStops.get(i).getSavedStopId());
                busStopCode.add(userSavedBusStops.get(i).getSavedStopCode());
                busStopName.add(userSavedBusStops.get(i).getSavedStopName());
              }

              performGETBusDeparturesRequest(busStopId, busStopCode, busStopName);
            }
          },
          throwable -> Log.e(TAG, "Unable to load user saved bus stops", throwable)));
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    if (mConnectivityManager != null) {
      UtilConnection.isInternetConnected(mConnectivityManager, mInternetConnectedCallback, false);
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.setting_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_setting:
        mSettingSelectedCallback.onSettingItemSelected();

        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }
}
