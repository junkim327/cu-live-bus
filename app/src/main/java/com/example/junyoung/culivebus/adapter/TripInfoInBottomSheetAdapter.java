package com.example.junyoung.culivebus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.db.entity.RouteInfo;
import com.example.junyoung.culivebus.util.TimeFormatter;
import com.example.junyoung.culivebus.vo.Itinerary;
import com.example.junyoung.culivebus.vo.Leg;
import com.example.junyoung.culivebus.vo.Service;
import com.example.junyoung.culivebus.httpclient.pojos.Walk;

import java.util.ArrayList;

public class TripInfoInBottomSheetAdapter extends Adapter<ViewHolder>{
  public static class WalkInfoBottomSheetViewHolder extends ViewHolder {
    public ImageView walkIconImageView;
    public TextView walkMinutesTextView;
    public TextView walkDistanceTextView;

    public WalkInfoBottomSheetViewHolder(View itemView) {
      super(itemView);

      walkIconImageView =
        itemView.findViewById(R.id.image_walk_icon_planned_trips_result_walk_card);
      walkMinutesTextView =
        itemView.findViewById(R.id.text_walk_minutes_planned_trips_result_walk_card);
      walkDistanceTextView =
        itemView.findViewById(R.id.text_walk_distance_planned_trips_result_walk_card);
    }
  }

  public static class BusInfoBottomSheetViewHolder extends ViewHolder {
    public ImageView busIconImageView;
    public TextView busNameTextView;
    public TextView busMinutesTextView;
    public TextView endPointNameTextView;
    public TextView startPointNameTextView;

    public BusInfoBottomSheetViewHolder(View itemView) {
      super(itemView);

      busIconImageView =
        itemView.findViewById(R.id.image_bus_icon_planned_trip_results_with_map_bus_card);
      busNameTextView =
        itemView.findViewById(R.id.text_bus_name_planned_trip_results_with_map_bus_card);
      busMinutesTextView =
        itemView.findViewById(R.id.text_bus_minutes_planned_trip_results_with_map_bus_card);
      endPointNameTextView =
        itemView.findViewById(R.id.text_end_point_name_planned_trip_results_with_map_bus_card);
      startPointNameTextView =
        itemView.findViewById(R.id.text_start_point_name_planned_trip_results_with_map_bus_card);
    }
  }

  public static class PlaceInfoBottomSheetViewHolder extends ViewHolder {
    public TextView startTimeTextView;
    public TextView startPointNameTextView;
    public ImageView iconImageView;

    public PlaceInfoBottomSheetViewHolder(View itemView) {
      super(itemView);

      iconImageView =
        itemView.findViewById(R.id.image_icon_planned_trip_results_with_map_place_card);
      startTimeTextView =
        itemView.findViewById(R.id.text_start_time_planned_trip_results_with_map_place_card);
      startPointNameTextView =
        itemView.findViewById(R.id.text_starting_point_name_planned_trip_results_with_map_place_card);
    }
  }

  private Context mContext;
  private Itinerary mItinerary;
  private RouteInfo mDirectionInfo;
  private ArrayList<Leg> busList;
  private ArrayList<Leg> walkList;

  public TripInfoInBottomSheetAdapter(Context context) {
    mContext = context;
  }

  public void updateDirectionInfo(RouteInfo directionInfo) {
    mDirectionInfo = directionInfo;
  }

  public void updateItinerary(Itinerary itinerary) {
    mItinerary = itinerary;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View walkInfoBottomSheetView =
      inflater.inflate(R.layout.planned_trip_results_with_map_walk_card, parent, false);
    if (viewType == 0) {
      View placeInfoBottomSheetView =
        inflater.inflate(R.layout.planned_trip_results_with_map_place_card, parent, false);
      return new PlaceInfoBottomSheetViewHolder(placeInfoBottomSheetView);
    }
    if (viewType == 2) {
      View busInfoBottomSheetView =
        inflater.inflate(R.layout.card_bus_planned_trip_results_with_map, parent, false);
      return new BusInfoBottomSheetViewHolder(busInfoBottomSheetView);
    }

    return new WalkInfoBottomSheetViewHolder(walkInfoBottomSheetView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    switch (holder.getItemViewType()) {
      case 0:
        PlaceInfoBottomSheetViewHolder placeInfoViewHolder =
          (PlaceInfoBottomSheetViewHolder) holder;
        if (position == 0) {
          String startTime = TimeFormatter.getTimeInDifferentForamt(
            mContext, mItinerary.getStartTime(), "24hr");
          placeInfoViewHolder.startTimeTextView.setText(startTime);
          placeInfoViewHolder.startPointNameTextView.setText(mDirectionInfo.getStartingPointName());
        } else if (position == (mItinerary.getLegs().size() + 1)) {
          String endTime = TimeFormatter.getTimeInDifferentForamt(
            mContext, mItinerary.getEndTime(), "24hr");
          placeInfoViewHolder.startTimeTextView.setText(endTime);
          placeInfoViewHolder.startPointNameTextView.setText(mDirectionInfo.getDestinationName());
          placeInfoViewHolder.iconImageView.setImageDrawable(
            mContext.getDrawable(R.drawable.destination_icon)
          );
        }
        break;
      case 1:
        WalkInfoBottomSheetViewHolder walkInfoViewHolder = (WalkInfoBottomSheetViewHolder) holder;
        Walk walk = mItinerary.getLegs().get(position - 1).getWalk();
        String walkMinutes = TimeFormatter.getTimeInterval(
          mContext,
          walk.getBegin().getTime(),
          walk.getEnd().getTime(),
          "min"
        );
        String walkDistance = walk.getDistance() + " mi";

        walkInfoViewHolder.walkMinutesTextView.setText(walkMinutes);
        walkInfoViewHolder.walkDistanceTextView.setText(walkDistance);
        break;
      case 2:
        BusInfoBottomSheetViewHolder busInfoViewHolder = (BusInfoBottomSheetViewHolder) holder;
        Service busService = mItinerary.getLegs().get(position - 1).getServices().get(0);
        String direction = busService.getTrip().getDirection();
        String routeLongName = busService.getRoute().getRouteLongName();
        String routeShortName = busService.getRoute().getRouteShortName();

        String busColor = "#" + busService.getRoute().getRouteColor();
        String endPointName = "Arrive at " + busService.getEnd().getName();
        String startPointName = "Board " + busService.getBegin().getName();
        String busName = getBusName(direction, routeLongName, routeShortName);
        String busMinutes = TimeFormatter.getTimeInterval(
          mContext,
          busService.getBegin().getTime(),
          busService.getEnd().getTime(),
          "min"
        );

        busInfoViewHolder.busNameTextView.setText(busName);
        busInfoViewHolder.busMinutesTextView.setText(busMinutes);
        busInfoViewHolder.endPointNameTextView.setText(endPointName);
        busInfoViewHolder.startPointNameTextView.setText(startPointName);
        busInfoViewHolder.busIconImageView.setColorFilter(
          Color.parseColor(busColor),
          PorterDuff.Mode.SRC_IN
        );
    }
  }

  @Override
  public int getItemCount() {
    return mItinerary != null ? (mItinerary.getLegs().size() + 2) : 0;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0 || position == (mItinerary.getLegs().size() + 1)) {
      return 0;
    } else if (mItinerary.getLegs().get(position - 1).getType()
      .contentEquals(mContext.getResources().getString(R.string.type_walk))) {
      return 1;
    }

    return 2;
  }

  private String getBusName(String direction, String routeLongName, String routeShortName) {
    StringBuilder builder = new StringBuilder(routeShortName);
    if (routeLongName.contains(" ")) {
      int lastIndex = routeLongName.indexOf(" ");
      routeLongName = routeLongName.substring(0, lastIndex);
    }

    return builder.append(direction.charAt(0)).append(" ").append(routeLongName).toString();
  }
}
