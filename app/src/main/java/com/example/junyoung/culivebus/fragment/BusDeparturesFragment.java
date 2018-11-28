package com.example.junyoung.culivebus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.example.junyoung.culivebus.room.entity.StopPoint;
import com.example.junyoung.culivebus.ui.Injection;
import com.example.junyoung.culivebus.ui.factory.UserSavedBusStopViewModelFactory;
import com.example.junyoung.culivebus.ui.viewmodel.BusDeparturesViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SavedBusStopViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SharedDepartureViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SharedStopPointViewModel;
import com.example.junyoung.culivebus.util.UtilConnection;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class BusDeparturesFragment extends Fragment
  implements ToggleButton.OnCheckedChangeListener {
  private static final int MAX_NUM_SAVED_STOPS = 3;
  private static final String TAG = BusDeparturesFragment.class.getSimpleName();

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
  private BusDeparturesViewModel mBusDeparturesViewModel;
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
  @BindView(R.id.textview_bus_stop_name_bus_departures)
  TextView mBusStopNameTextView;
  @BindView(R.id.textview_bus_stop_code_bus_departures)
  TextView mBusStopCodeTextView;
  @BindView(R.id.progressbar_bus_departures)
  ProgressBar mProgressBar;
  @BindView(R.id.text_no_departures_bus_departures)
  TextView mNoDeparturesTextView;
  @BindView(R.id.recycler_view_bus_departures)
  RecyclerView mRecyclerView;
  @BindView(R.id.toggle_button_bus_departures)
  ToggleButton mAddFavoriteButton;
  @BindView(R.id.image_unselected_button_bus_departures)
  ImageView unselectedButtonImageView;

  public interface OnDepartureClickedListener {
    void onDepartureClicked();
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
      view = inflater.inflate(R.layout.fragment_bus_departures, container, false);
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
    mBusStopNameTextView.setText(busStopName);
    mBusStopCodeTextView.setText(busStopCode);

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
    mRecyclerView.setHasFixedSize(true);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerView.setLayoutManager(layoutManager);

    RecyclerviewClickListener listener = (view, position) -> {
      List<SortedDeparture> sortedDepartureList =
        mBusDeparturesViewModel.getSortedDepartureList().getValue();
      if (sortedDepartureList != null) {
        mSharedDepartureViewModel.select(sortedDepartureList.get(position));
        mDepartureClickedCallback.onDepartureClicked();
      }

    };
    mAdapter = new BusDeparturesAdapter(getContext(), listener);
    mRecyclerView.setAdapter(mAdapter);
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

    mBusDeparturesViewModel = ViewModelProviders.of(getActivity()).get(BusDeparturesViewModel.class);
    mBusDeparturesViewModel.init(mStopPoint.getStopId());
    mBusDeparturesViewModel.getSortedDepartureList().observe(this, sortedDepartureList -> {
      mProgressBar.setVisibility(INVISIBLE);
      if (sortedDepartureList != null) {
        Log.d(TAG, "Update sortedDepartureList, Size: " + sortedDepartureList.size());
        mSize = sortedDepartureList.size();
        if (mSize == 0) {
          showNoDeparturesMessage();
        } else {
          if (mRecyclerView.getVisibility() == GONE) {
            mNoDeparturesTextView.setVisibility(GONE);
            mRecyclerView.setVisibility(VISIBLE);
          }
          mAdapter.updateDepartureList(sortedDepartureList);
        }
      }
    });
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
    mRecyclerView.setVisibility(GONE);
    mNoDeparturesTextView.setVisibility(VISIBLE);
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.d(TAG, "onPause has called");
    for (int i = 0; i < mSize; i++) {
      mAdapter.cancelTimer(mRecyclerView.findViewHolderForAdapterPosition(i));
    }

    mBusDeparturesViewModel.cancelTimer();
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
