package com.example.junyoung.uiucbus.fragments;

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

import com.example.junyoung.uiucbus.CustomLinearLayoutManager;
import com.example.junyoung.uiucbus.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.RecyclerviewClickListener;
import com.example.junyoung.uiucbus.adapters.PlannedTripResultsAdapter;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.httpclient.pojos.Itinerary;
import com.example.junyoung.uiucbus.httpclient.pojos.Leg;
import com.example.junyoung.uiucbus.utils.UtilConnection;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlannedTripsResultFragment extends Fragment {
  public static final String EXTRA_ITINERARIES =
    "com.example.junyoung.uiucbus.fragments.EXTRA_ITINERARIES";

  private ArrayList<Leg> busList = null;
  private ArrayList<Leg> walkList = null;
  private ArrayList<Itinerary> itineraries = null;
  private ConnectivityManager mConnectivityManager;
  private OnInternetConnectedListener mInternetConnectedCallback;
  private PlannedTripResultsClickListener onPlannedTripResultsCallback;

  @BindView(R.id.recyclerview_planned_trips_result)
  RecyclerView recyclerView;
  @BindView(R.id.textview_no_transit_routes_planned_trips_result)
  TextView noTransitRoutesTextView;
  @BindView(R.id.imageview_no_transit_routes_planned_trips_result)
  ImageView noTransitRoutesImageView;

  public interface PlannedTripResultsClickListener {
    void onPlannedTripResultsClickListener(Itinerary itinerary,
                                           ArrayList<Leg> busList,
                                           ArrayList<Leg> walkList);
  }

  public static PlannedTripsResultFragment newInstance(ArrayList<Itinerary> itineraries) {
    PlannedTripsResultFragment fragment = new PlannedTripsResultFragment();
    Bundle args = new Bundle();
    args.putParcelableArrayList(EXTRA_ITINERARIES, itineraries);
    fragment.setArguments(args);

    return fragment;
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
      onPlannedTripResultsCallback = (PlannedTripResultsClickListener) context;
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

    if (getArguments() != null) {
      this.itineraries = getArguments().getParcelableArrayList(EXTRA_ITINERARIES);
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    boolean isConnected  = true;
    if (mConnectivityManager != null) {
      isConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, true);
    }

    if (isConnected) {
      View view = inflater.inflate(R.layout.fragment_planned_trips_result, container, false);
      ButterKnife.bind(this, view);

      if (itineraries == null || itineraries.size() == 0) {
        recyclerView.setVisibility(View.GONE);
        noTransitRoutesTextView.setVisibility(View.VISIBLE);
        noTransitRoutesImageView.setVisibility(View.VISIBLE);
      } else {
        recyclerView.setVisibility(View.VISIBLE);
        noTransitRoutesTextView.setVisibility(View.GONE);
        noTransitRoutesImageView.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        LayoutManager layoutManager = new CustomLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
          getContext(),
          R.anim.layout_animation_fall_down_long
        );
        recyclerView.setLayoutAnimation(animation);

        RecyclerviewClickListener listener = (view1, position) -> {
          setBusandWalkList(itineraries.get(position));
          onPlannedTripResultsCallback.onPlannedTripResultsClickListener(
            itineraries.get(position),
            busList,
            walkList
          );
        };
        RecyclerView.Adapter adapter = new PlannedTripResultsAdapter(
          getContext(),
          itineraries,
          listener
        );
        recyclerView.setAdapter(adapter);
      }

      return view;
    }

    return null;
  }

  private void setBusandWalkList(Itinerary itinerary) {
    busList = new ArrayList<>();
    walkList = new ArrayList<>();
    for (Leg leg : itinerary.getLegs()) {
      if (leg.getType().contentEquals(getResources().getString(R.string.type_service))) {
        busList.add(leg);
      } else {
        walkList.add(leg);
      }
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