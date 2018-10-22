package com.example.junyoung.uiucbus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.RecyclerviewClickListener;
import com.example.junyoung.uiucbus.utils.TimeFormatter;
import com.example.junyoung.uiucbus.httpclient.pojos.Itinerary;

import java.util.ArrayList;

public class PlannedTripResultsAdapter
  extends RecyclerView.Adapter<PlannedTripResultsAdapter.PlannedTripsViewHolder> {
  public static class PlannedTripsViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
    public TextView travelTimeTextView;
    public TextView lessWalkingTextView;
    public TextView timeIntervalTextView;
    public RecyclerView tripsInfoRecyclerView;
    public RecyclerviewClickListener recyclerviewClickListener;

    public PlannedTripsViewHolder(View itemView, RecyclerviewClickListener listener) {
      super(itemView);

      this.recyclerviewClickListener = listener;
      itemView.setOnClickListener(this);

      travelTimeTextView = itemView.findViewById(R.id.textview_travel_time_planned_trips_card);
      lessWalkingTextView =itemView.findViewById(R.id.textview_less_walking_planned_trips_card);
      timeIntervalTextView = itemView.findViewById(R.id.textview_time_interval_planned_trips_card);
      tripsInfoRecyclerView =
        itemView.findViewById(R.id.recyclerview_trips_info_planned_trips_card);
    }

    @Override
    public void onClick(View view) {
      int pos = getAdapterPosition();
      if (pos != RecyclerView.NO_POSITION) {
        recyclerviewClickListener.onClick(view, pos);
      }
    }
  }

  private Context context;
  private ArrayList<Itinerary> itineraries = null;
  private RecyclerviewClickListener recyclerviewClickListenerlistener;

  public PlannedTripResultsAdapter(Context context,
                                   ArrayList<Itinerary> itineraries,
                                   RecyclerviewClickListener listener) {
    this.context = context;
    this.itineraries = itineraries;
    this.recyclerviewClickListenerlistener = listener;
  }

  @NonNull
  @Override
  public PlannedTripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View plannedTripsView = inflater.inflate(R.layout.planned_trip_results_with_map_card, parent, false);
    PlannedTripsViewHolder vh = new PlannedTripsViewHolder(
      plannedTripsView,
      recyclerviewClickListenerlistener
    );

    return vh;
  }

  @Override
  public void onBindViewHolder(@NonNull PlannedTripsViewHolder holder, int position) {
    String travelTime = itineraries.get(position).getTravelTime() + "min";
    String startTime = itineraries.get(position).getStartTime();
    String endTime = itineraries.get(position).getEndTime();
    String intervalTime = TimeFormatter.getTimeInterval(context, startTime, endTime, "24hr");

    if (position == 0) {
      holder.lessWalkingTextView.setVisibility(View.VISIBLE);
    }
    holder.travelTimeTextView.setText(travelTime);
    holder.timeIntervalTextView.setText(intervalTime);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
    holder.tripsInfoRecyclerView.setLayoutManager(layoutManager);
    TripInfoAdapter adapter = new TripInfoAdapter(context, itineraries.get(position).getLegs());
    holder.tripsInfoRecyclerView.setAdapter(adapter);

  }

  @Override
  public int getItemCount() {
    return itineraries.size();
  }
}
