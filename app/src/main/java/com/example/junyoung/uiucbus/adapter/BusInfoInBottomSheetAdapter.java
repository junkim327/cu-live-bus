package com.example.junyoung.uiucbus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.httpclient.pojos.Leg;
import com.example.junyoung.uiucbus.httpclient.pojos.Service;

import java.util.ArrayList;

/**
 * Created by JunYoung on 2018. 9. 16..
 */

public class BusInfoInBottomSheetAdapter
  extends Adapter<BusInfoInBottomSheetAdapter.BusInfoInBottomSheetViewHolder> {
  public static class BusInfoInBottomSheetViewHolder extends ViewHolder {
    public TextView busNameTextView;
    public ImageView busIconImageView;

    public BusInfoInBottomSheetViewHolder(View itemView) {
      super(itemView);

      busNameTextView = itemView.findViewById(R.id.text_bus_name_bus_info_card_in_bottom_sheet);
      busIconImageView = itemView.findViewById(R.id.image_bus_icon_bus_info_card_in_bottom_sheet);
    }
  }

  private Context context;
  private ArrayList<Leg> busList = null;

  public BusInfoInBottomSheetAdapter(Context context, ArrayList<Leg> busList) {
    this.context = context;
    this.busList = busList;
  }

  @NonNull
  @Override
  public BusInfoInBottomSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int
    viewType) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View busInfoInBottomSheetView = inflater.inflate(
      R.layout.bus_info_card_in_bottom_sheet_planned_trip_results,
      parent,
      false
    );
    BusInfoInBottomSheetViewHolder vh =
      new BusInfoInBottomSheetViewHolder(busInfoInBottomSheetView);

    return vh;
  }

  @Override
  public void onBindViewHolder(@NonNull BusInfoInBottomSheetViewHolder holder, int position) {
    String busColor = "#" + busList.get(position).getServices().get(0).getRoute().getRouteColor();
    holder.busNameTextView.setText(getBusName(busList.get(position).getServices().get(0)));
    holder.busIconImageView.setColorFilter(Color.parseColor(busColor), PorterDuff.Mode.SRC_IN);
  }

  @Override
  public int getItemCount() {
    return busList.size();
  }

  public String getBusName(Service service) {
    String direction = service.getTrip().getDirection();
    String routeLongName = service.getRoute().getRouteLongName();
    String routeShortName = service.getRoute().getRouteShortName();
    StringBuilder builder = new StringBuilder(routeShortName);
    if (routeLongName.contains(" ")) {
      int lastIndex = routeLongName.indexOf(" ");
      routeLongName = routeLongName.substring(0, lastIndex);
    }

    return builder.append(direction.charAt(0)).append(" ").append(routeLongName).toString();
  }
}
