package com.example.junyoung.culivebus.fragment;

import androidx.databinding.DataBindingComponent;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.junyoung.culivebus.binding.FragmentDataBindingComponent;
import com.example.junyoung.culivebus.databinding.DepartureFragmentBinding;
import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.example.junyoung.culivebus.ui.common.NavigationController;
import com.example.junyoung.culivebus.util.AutoClearedValue;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.junyoung.culivebus.util.listener.OnHomeItemClickedListener;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;
import com.example.junyoung.culivebus.adapter.BusDeparturesAdapter;
import com.example.junyoung.culivebus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.culivebus.ui.Injection;
import com.example.junyoung.culivebus.ui.factory.UserSavedBusStopViewModelFactory;
import com.example.junyoung.culivebus.ui.viewmodel.BusDepartureViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SavedBusStopViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SharedDepartureViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SharedStopPointViewModel;
import com.example.junyoung.culivebus.util.UtilConnection;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class DepartureFragment extends Fragment
  implements ToggleButton.OnCheckedChangeListener {
  private static final int MAX_NUM_SAVED_STOPS = 3;
  private static final String TAG = DepartureFragment.class.getSimpleName();

  private static final String DEPARTURE_STOP_ID_KEY = "departure_stop_id";
  private static final String DEPARTURE_STOP_NAME_KEY = "departure_stop_name";
  private static final String DEPARTURE_STOP_CODE_KEY = "departure_stop_code";

  @Inject
  ViewModelProvider.Factory viewModelFactory;
  @Inject
  NavigationController navigationController;

  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<DepartureFragmentBinding> binding;

  private int mNumSavedBusStops;
  private int mSize = 0;
  private String mUid;

  private Unbinder mUnbinder;
  private StopPoint mStopPoint;
  private BusDeparturesAdapter mAdapter;
  private ConnectivityManager mConnectivityManager;
  private OnHomeItemClickedListener mHomeItemClickedCallback;
  private OnDepartureClickedListener mDepartureClickedCallback;
  private OnInternetConnectedListener mInternetConnectedCallback;
  private BusDepartureViewModel mBusDepartureViewModel;
  private SharedDepartureViewModel mSharedDepartureViewModel;
  private SharedStopPointViewModel mSharedStopPointViewModel;
  private SavedBusStopViewModel mSavedBusStopViewModel;
  private UserSavedBusStopViewModelFactory mUserSavedBusStopViewModelFactory;
  private CompositeDisposable mDisposable = new CompositeDisposable();

  @BindView(R.id.toolbar_bus_departures)
  Toolbar mToolbar;
  @BindView(R.id.appbar_layout_bus_departures)
  AppBarLayout mAppBarLayout;
  @BindView(R.id.collapsing_toolbar_layout_bus_departures)
  CollapsingToolbarLayout mCollapsingToolbarLayout;
  @BindView(R.id.progressbar_bus_departures)
  ProgressBar mProgressBar;
  @BindView(R.id.text_no_departures_bus_departures)
  TextView mNoDeparturesTextView;
  @BindView(R.id.toggle_button_bus_departures)
  ToggleButton mAddFavoriteButton;
  @BindView(R.id.image_unselected_button_bus_departures)
  ImageView unselectedButtonImageView;

  public interface OnDepartureClickedListener {
    void onDepartureClicked();
  }

  public static DepartureFragment create(@NonNull StopPoint stopPoint) {
    DepartureFragment departureFragment = new DepartureFragment();
    Bundle args = new Bundle();
    args.putString(DEPARTURE_STOP_ID_KEY, stopPoint.getStopId());
    args.putString(DEPARTURE_STOP_NAME_KEY, stopPoint.getStopName());
    args.putString(DEPARTURE_STOP_CODE_KEY, stopPoint.getStopCode());
    departureFragment.setArguments(args);

    return departureFragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );

    try {
      mDepartureClickedCallback = (OnDepartureClickedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnDepartureClickedListener.");
    }

    try {
      mInternetConnectedCallback = (OnInternetConnectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnInternetConnectedListener.");
    }

    try {
      mHomeItemClickedCallback = (OnHomeItemClickedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnHomeItemClickedListener.");
    }
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mUserSavedBusStopViewModelFactory =
      Injection.provideUserSavedBusStopViewModelFactory(getContext());
    mSavedBusStopViewModel = ViewModelProviders.of(this, mUserSavedBusStopViewModelFactory)
      .get(SavedBusStopViewModel.class);

    if (mUid != null && mStopPoint != null) {
      mDisposable.add(mSavedBusStopViewModel.getNumBusStopsByUid(mUid, mStopPoint.getStopId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(numBusStops -> {
          Log.d(TAG, "Num : " + numBusStops);
          if (numBusStops == 1) {
            mAddFavoriteButton.setChecked(true);
            mAddFavoriteButton.setClickable(true);
          }
          mAddFavoriteButton.setOnCheckedChangeListener(this);
        }));
    }

    mSharedDepartureViewModel = ViewModelProviders.of(getActivity())
      .get(SharedDepartureViewModel.class);

    mBusDepartureViewModel = ViewModelProviders.of(this, viewModelFactory)
      .get(BusDepartureViewModel.class);
    mBusDepartureViewModel.init(mStopPoint.getStopId());
    mBusDepartureViewModel.getSortedDepartureList().observe(this, sortedDepartureList -> {
      mProgressBar.setVisibility(INVISIBLE);
      if (sortedDepartureList != null) {
        Log.d(TAG, "Update sortedDepartureList, Size: " + sortedDepartureList.size());
        mSize = sortedDepartureList.size();
        if (mSize == 0) {
          showNoDeparturesMessage();
        } else {
          /*
          if (mRecyclerView.getVisibility() == GONE) {
            mNoDeparturesTextView.setVisibility(GONE);
            mRecyclerView.setVisibility(VISIBLE);
          } */
          mAdapter.updateDepartureList(sortedDepartureList);
        }
      }
    });
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );

    mUid = sharedPref.getString(getString(R.string.saved_uid), null);
    mNumSavedBusStops = sharedPref.getInt(getResources().getString(R.string.saved_bus_stops), 0);

    Log.d(TAG, "Num saved bus stops: " + mNumSavedBusStops);

    mSharedStopPointViewModel = ViewModelProviders.of(getActivity())
      .get(SharedStopPointViewModel.class);
    mStopPoint = mSharedStopPointViewModel.getSelectedStopPoint().getValue();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    boolean isInternetConnected = true;
    if (mConnectivityManager != null) {
      isInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, false);
    }

    View view = null;
    if (isInternetConnected) {
      view = inflater.inflate(R.layout.departure_fragment, container, false);
      mUnbinder = ButterKnife.bind(this, view);

      if (mNumSavedBusStops == MAX_NUM_SAVED_STOPS) {
        mAddFavoriteButton.setClickable(false);
      }

      mAddFavoriteButton.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
          if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "ACTION_DOWN");
            if (mNumSavedBusStops == MAX_NUM_SAVED_STOPS) {
              displayToast("You can add up to 3 bus stops", getActivity());
            }
          }
          return false;
        }
      });

      if (mStopPoint != null) {
        setAppBarLayout(mStopPoint.getStopName(), mStopPoint.getStopCode());
      }
      setToolbar();
      setRecyclerView();
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

  private void setAppBarLayout(String busStopName, String busStopCode) {
    mCollapsingToolbarLayout.setTitleEnabled(true);

    mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      boolean isShow = true;
      int scrollRange = -1;

      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
          isShow = true;
          mCollapsingToolbarLayout.setTitle(busStopCode);
          mCollapsingToolbarLayout.setCollapsedTitleTextColor(
            getResources().getColor(R.color.white)
          );
        } else if (isShow) {
          isShow = false;
          mCollapsingToolbarLayout.setTitle(" ");
        }
      }
    });
  }

  private void setRecyclerView() {
    //mRecyclerView.setHasFixedSize(true);

    RecyclerviewClickListener listener = (view, position) -> {
      List<SortedDeparture> sortedDepartureList =
        mBusDepartureViewModel.getSortedDepartureList().getValue();
      if (sortedDepartureList != null) {
        mSharedDepartureViewModel.select(sortedDepartureList.get(position));
        mDepartureClickedCallback.onDepartureClicked();
      }

    };
    mAdapter = new BusDeparturesAdapter(getContext(), listener);
    //mRecyclerView.setAdapter(mAdapter);
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

  @Override
  public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
    if (isChecked) {
      if (mStopPoint != null) {
        mDisposable.add(mSavedBusStopViewModel.insertBusStop(mUid, mStopPoint.getStopId(),
          mStopPoint.getStopCode(), mStopPoint.getStopName())
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(() -> {
            Log.i(TAG, "Bus stop is successfully stored in the database.");
            displayToast("Added to Favorites", getActivity());
          }, throwable -> Log.e(TAG, "Unable to insert bus stop :(", throwable))
        );
        mNumSavedBusStops++;
      }
    } else {
      if (mStopPoint != null) {
        mDisposable.add(mSavedBusStopViewModel.deleteBusStopByUidAndStopId(mUid,
          mStopPoint.getStopId())
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(() -> {
            Log.i(TAG, "Bus stop is successfully deleted from the database");
            displayToast("Removed from Favorites", getActivity());
          }, throwable -> Log.e(TAG, "Unable to delete bus stop :(", throwable))
        );
        mNumSavedBusStops--;
      }
    }

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putInt(getString(R.string.saved_bus_stops), mNumSavedBusStops);
    editor.apply();
  }

  private void showNoDeparturesMessage() {
    Random random = new Random();
    int max = 3;
    int min = 1;
    int generatedNum = random.nextInt(max - min + 1) + min;
    if (generatedNum == 1) {
      mNoDeparturesTextView.setText(getResources().getText(
        R.string.message_when_no_departures_one));
    } else if (generatedNum == 2) {
      mNoDeparturesTextView.setText(getResources().getText(
        R.string.message_when_no_departures_two
      ));
    } else {
      mNoDeparturesTextView.setText(getResources().getText(
        R.string.message_when_no_departures_three
      ));
    }
    //mRecyclerView.setVisibility(GONE);
    mNoDeparturesTextView.setVisibility(VISIBLE);
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.d(TAG, "onPause has called");
    for (int i = 0; i < mSize; i++) {
      //mAdapter.cancelTimer(mRecyclerView.findViewHolderForAdapterPosition(i));
    }

    mBusDepartureViewModel.cancelTimer();
  }

  @Override
  public void onStop() {
    super.onStop();
    Log.d(TAG, "onStop has called");
    mDisposable.dispose();
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
        mHomeItemClickedCallback.onHomeItemClicked();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    if (mUnbinder != null) {
      mUnbinder.unbind();
      mUnbinder = null;
    }
  }
}
