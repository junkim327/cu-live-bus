package com.example.junyoung.uiucbus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.utils.TimeFormatter;
import com.example.junyoung.uiucbus.httpclient.pojos.Leg;
import com.example.junyoung.uiucbus.httpclient.pojos.Service;

import java.util.ArrayList;

import static android.view.View.GONE;

public class TripInfoAdapter extends RecyclerView.Adapter<TripInfoAdapter.TripsInfoViewHolder> {
  public static class TripsInfoViewHolder extends RecyclerView.ViewHolder {
    public TextView endPointInfoTextView;
    public TextView busNameTextView;
    public TextView startingPointInfoTextView;
    public ImageView busIconImageView;

    public TripsInfoViewHolder(View itemView) {
      super(itemView);

      busNameTextView = itemView.findViewById(R.id.textview_bus_name_trips_info_card);
      busIconImageView = itemView.findViewById(R.id.imageview_bus_icon_trips_info_card);
      endPointInfoTextView = itemView.findViewById(R.id.textview_end_point_info_tips_info_card);
      startingPointInfoTextView = itemView.findViewById(R.id.textview_starting_point_info_trips_info_card);
    }
  }

  private Context context;
  private ArrayList<Leg> legs = null;
  private ArrayList<Service> services = null;

  public TripInfoAdapter(Context context, ArrayList<Leg> legs) {
    this.legs = legs;
    this.context = context;
  }

  @NonNull
  @Override
  public TripsInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View tripsInfoView = inflater.inflate(R.layout.card_trips_info, parent, false);
    TripsInfoViewHolder vh = new TripsInfoViewHolder(tripsInfoView);

    return vh;
  }

  @Override
  public void onBindViewHolder(@NonNull TripsInfoViewHolder holder, int position) {
    Leg leg = legs.get(position);
    if (leg.getType().contentEquals(context.getResources().getString(R.string.type_service))) {
      Service service = leg.getServices().get(0);
      String direction = service.getTrip().getDirection();
      String routeLongName = service.getRoute().getRouteLongName();
      String routeShortName = service.getRoute().getRouteShortName();
      String busName = getBusName(direction, routeLongName, routeShortName);
      String busColor = "#" + service.getRoute().getRouteColor();
      String startingPointName = "Board " + service.getBegin().getName();
      String endPointName = "Arrive at " + service.getEnd().getName();

      holder.busNameTextView.setText(busName);
      holder.endPointInfoTextView.setText(endPointName);
      holder.startingPointInfoTextView.setText(startingPointName);
      holder.busIconImageView.setColorFilter(Color.parseColor(busColor), PorterDuff.Mode.SRC_IN);
    } else {
      holder.endPointInfoTextView.setVisibility(GONE);
      holder.startingPointInfoTextView.setVisibility(GONE);

      String startTime = leg.getWalk().getBegin().getTime();
      String endTime = leg.getWalk().getEnd().getTime();
      String timeInterval = TimeFormatter.getTimeInterval(context, startTime, endTime, "24hr");

      holder.busNameTextView.setText(timeInterval);
      holder.busIconImageView.setImageResource(R.drawable.ic_directions_walk);
    }
  }

  @Override
  public int getItemCount() {
    return legs.size();
  }

  private String getBusName(String direction, String routeLongName, String routeShortName) {
    StringBuilder builder = new StringBuilder(routeShortName);
    if (routeLongName.contains(" ")) {
      int lastIndex = routeLongName.indexOf(" ");
      routeLongName = routeLongName.substring(0, lastIndex);
    }

    return builder.append(direction.charAt(0)).append(" ").append(routeLongName).toString();
  }

  public ArrayList<Service> getServices(ArrayList<Leg> legs) {
    ArrayList<Service> services = new ArrayList<>();
    for (Leg leg : legs) {
      if (leg.getType().contentEquals(context.getResources().getString(R.string.type_service))) {
        services.add(leg.getServices().get(0));
      }
    }

    return services;
  }
}
