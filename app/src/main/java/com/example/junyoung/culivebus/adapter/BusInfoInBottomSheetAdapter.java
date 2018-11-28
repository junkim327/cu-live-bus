package com.example.junyoung.culivebus.adapter;

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

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.httpclient.pojos.Leg;
import com.example.junyoung.culivebus.httpclient.pojos.Service;

import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;

public class BusInfoInBottomSheetAdapter
  extends Adapter<BusInfoInBottomSheetAdapter.BusInfoInBottomSheetViewHolder> {
  public static class BusInfoInBottomSheetViewHolder extends ViewHolder {
    public TextView busNameTextView;
    public ImageView busIconImageView;
    public ImageView rightArrowImageView;

    public BusInfoInBottomSheetViewHolder(View itemView) {
      super(itemView);

      busNameTextView = itemView.findViewById(R.id.text_bus_name_bus_info_card_in_bottom_sheet);
      busIconImageView = itemView.findViewById(R.id.image_bus_icon_bus_info_card_in_bottom_sheet);
      rightArrowImageView =
        itemView.findViewById(R.id.image_right_arrow_bus_info_card_in_bottom_sheet);
    }
  }

  private Context mContext;
  private List<Leg> mBusList = Collections.emptyList();

  public BusInfoInBottomSheetAdapter(Context context) {
    mContext = context;
  }

  public void updateBusList(List<Leg> busList) {
    mBusList = busList;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public BusInfoInBottomSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int
    viewType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View busInfoInBottomSheetView = inflater.inflate(
      R.layout.card_bus_info_bottom_sheet_planned_trip_results,
      parent,
      false
    );
    BusInfoInBottomSheetViewHolder vh =
      new BusInfoInBottomSheetViewHolder(busInfoInBottomSheetView);

    return vh;
  }

  @Override
  public void onBindViewHolder(@NonNull BusInfoInBottomSheetViewHolder holder, int position) {
    String busColor = mContext.getResources().getString(
      R.string.hex_color,
      mBusList.get(position).getServices().get(0).getRoute().getRouteColor()
    );
    holder.busIconImageView.setColorFilter(Color.parseColor(busColor), PorterDuff.Mode.SRC_IN);
    holder.busNameTextView.setText(getBusName(mBusList.get(position).getServices().get(0)));
    if (position == (mBusList.size() - 1)) {
      holder.rightArrowImageView.setVisibility(GONE);
    }
  }

  @Override
  public int getItemCount() {
    return mBusList != null ? mBusList.size() : 0;
  }

  private String getBusName(Service service) {
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
