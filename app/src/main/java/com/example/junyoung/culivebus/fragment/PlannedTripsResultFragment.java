package com.example.junyoung.culivebus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junyoung.culivebus.util.CustomLinearLayoutManager;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;
import com.example.junyoung.culivebus.adapter.PlannedTripResultAdapter;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.httpclient.pojos.Itinerary;
import com.example.junyoung.culivebus.httpclient.pojos.Leg;
import com.example.junyoung.culivebus.ui.viewmodel.SharedItineraryViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.SharedTripViewModel;
import com.example.junyoung.culivebus.util.UtilConnection;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlannedTripsResultFragment extends Fragment {
  private boolean mIsConnected = true;

  private List<Leg> busList = Collections.emptyList();
  private List<Leg> walkList = Collections.emptyList();

  // TODO: Change ArrayList to List
  private PlannedTripResultAdapter mAdapter;
  private ConnectivityManager mConnectivityManager;

  // View models
  private SharedTripViewModel mSharedTripViewModel;
  private SharedItineraryViewModel mSharedItineraryViewModel;

  // Callbacks
  private OnInternetConnectedListener mInternetConnectedCallback;
  private PlannedTripResultClickListener onPlannedTripResultsCallback;

  @BindView(R.id.recyclerview_planned_trips_result)
  RecyclerView mRecyclerView;
  @BindView(R.id.textview_no_transit_routes_planned_trips_result)
  TextView mNoTransitRoutesTextView;
  @BindView(R.id.imageview_no_transit_routes_planned_trips_result)
  ImageView mNoTransitRoutesImageView;

  public interface PlannedTripResultClickListener {
    void onPlannedTripResultClicked();
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
      onPlannedTripResultsCallback = (PlannedTripResultClickListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnPlannedTripResultsClickListener."
      );
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );
    mSharedTripViewModel = ViewModelProviders.of(getActivity()).get(SharedTripViewModel.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    if (mConnectivityManager != null) {
      mIsConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, true);
    }

    View view = null;
    if (mIsConnected) {
      view = inflater.inflate(R.layout.fragment_planned_trips_result, container, false);
      ButterKnife.bind(this, view);

      setRecyclerView();
    }

    return view;
  }

  private void setRecyclerView() {
    mRecyclerView.setHasFixedSize(true);

    LayoutManager layoutManager = new CustomLinearLayoutManager(getContext());
    mRecyclerView.setLayoutManager(layoutManager);

    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
      getActivity(),
      R.anim.layout_animation_fall_down_long
    );
    mRecyclerView.setLayoutAnimation(animation);

    RecyclerviewClickListener listener = (view1, position) -> {
      mSharedTripViewModel.select(mAdapter.getItinerary(position));
      onPlannedTripResultsCallback.onPlannedTripResultClicked();
    };
    mAdapter = new PlannedTripResultAdapter(getContext(), listener);
    mRecyclerView.setAdapter(mAdapter);
  }

  private void setBusandWalkList(Itinerary itinerary) {
    for (Leg leg : itinerary.getLegs()) {
      if (leg.getType().contentEquals(getResources().getString(R.string.type_service))) {
        busList.add(leg);
      } else {
        walkList.add(leg);
      }
    }
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mSharedItineraryViewModel = ViewModelProviders.of(getActivity())
      .get(SharedItineraryViewModel.class);
    mSharedItineraryViewModel.getItineraryList().observe(this, itineraryList -> {
      if (itineraryList != null) {
        mAdapter.updateItineraryList(itineraryList);
        controlViewVisibility(itineraryList);
      }
    });
  }

  private void controlViewVisibility(List<Itinerary> itineraryList) {
    if (itineraryList == null || itineraryList.size() == 0) {
      mRecyclerView.setVisibility(View.GONE);
      mNoTransitRoutesTextView.setVisibility(View.VISIBLE);
      mNoTransitRoutesImageView.setVisibility(View.VISIBLE);
    } else {
      mRecyclerView.setVisibility(View.VISIBLE);
      mNoTransitRoutesTextView.setVisibility(View.GONE);
      mNoTransitRoutesImageView.setVisibility(View.GONE);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mConnectivityManager != null) {
      UtilConnection.isInternetConnected(mConnectivityManager, mInternetConnectedCallback, true);
    }
  }
}