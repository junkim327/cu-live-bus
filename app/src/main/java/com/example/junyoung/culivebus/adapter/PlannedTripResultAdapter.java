package com.example.junyoung.culivebus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;
import com.example.junyoung.culivebus.util.TimeFormatter;
import com.example.junyoung.culivebus.httpclient.pojos.Itinerary;

import java.util.Collections;
import java.util.List;

public class PlannedTripResultAdapter
  extends RecyclerView.Adapter<PlannedTripResultAdapter.PlannedTripsViewHolder> {
  public static class PlannedTripsViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
    public TextView mTravelTimeTextView;
    public TextView mLessWalkingTextView;
    public TextView mTimeIntervalTextView;
    public RecyclerView mTripsInfoRecyclerView;
    public RecyclerviewClickListener mRecyclerviewClickListener;

    public PlannedTripsViewHolder(View itemView, RecyclerviewClickListener listener) {
      super(itemView);

      mRecyclerviewClickListener = listener;
      itemView.setOnClickListener(this);

      mTravelTimeTextView = itemView.findViewById(R.id.textview_travel_time_planned_trips_card);
      mLessWalkingTextView =itemView.findViewById(R.id.textview_less_walking_planned_trips_card);
      mTimeIntervalTextView = itemView.findViewById(R.id.textview_time_interval_planned_trips_card);
      mTripsInfoRecyclerView =
        itemView.findViewById(R.id.recyclerview_trips_info_planned_trips_card);
    }

    @Override
    public void onClick(View view) {
      int pos = getAdapterPosition();
      if (pos != RecyclerView.NO_POSITION) {
        mRecyclerviewClickListener.onClick(view, pos);
      }
    }
  }

  private static final String TAG = PlannedTripResultAdapter.class.getSimpleName();
  private Context mContext;
  private List<Itinerary> mItineraryList = Collections.emptyList();
  private RecyclerviewClickListener mListener;

  public PlannedTripResultAdapter(Context context, RecyclerviewClickListener listener) {
    mContext = context;
    mListener = listener;
  }

  public void updateItineraryList(List<Itinerary> itineraryList) {
    mItineraryList = itineraryList;
    notifyDataSetChanged();
  }

  public Itinerary getItinerary(int pos) {
    return mItineraryList.get(pos);
  }

  @NonNull
  @Override
  public PlannedTripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Log.d(TAG, "onCreateViewHolder has called.");
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View plannedTripsView = inflater.inflate(R.layout.card_planned_trip_results_with_map, parent, false);
    PlannedTripsViewHolder vh = new PlannedTripsViewHolder(plannedTripsView, mListener);

    return vh;
  }

  @Override
  public void onBindViewHolder(@NonNull PlannedTripsViewHolder holder, int position) {
    String travelTime = mItineraryList.get(position).getTravelTime() + "min";
    String startTime = mItineraryList.get(position).getStartTime();
    String endTime = mItineraryList.get(position).getEndTime();
    String intervalTime = TimeFormatter.getTimeInterval(mContext, startTime, endTime, "24hr");

    if (position == 0) {
      holder.mLessWalkingTextView.setVisibility(View.VISIBLE);
    }
    holder.mTravelTimeTextView.setText(travelTime);
    holder.mTimeIntervalTextView.setText(intervalTime);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
    holder.mTripsInfoRecyclerView.setLayoutManager(layoutManager);
    TripInfoAdapter adapter = new TripInfoAdapter(mContext, mItineraryList.get(position).getLegs());
    holder.mTripsInfoRecyclerView.setAdapter(adapter);

  }

  @Override
  public int getItemCount() {
    return mItineraryList.size();
  }
}
