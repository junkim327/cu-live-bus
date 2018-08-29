package com.example.junyoung.uiucbus;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.uiucbus.httpclient.pojos.StopPoint;

import java.util.ArrayList;

public class BusStopsAdapter extends RecyclerView.Adapter<BusStopsAdapter.BusStopsViewHolder> {
  public static class BusStopsViewHolder extends RecyclerView.ViewHolder {
    public TextView busStopNameTextView;

    public BusStopsViewHolder(View itemView) {
      super(itemView);

      busStopNameTextView = itemView.findViewById(R.id.textview_bus_stop_name_bus_stops_card);
    }
  }

  private Context context;
  private ArrayList<StopPoint> busStops;

  public BusStopsAdapter(Context context, ArrayList<StopPoint> busStops) {
    this.context = context;
    this.busStops = busStops;
  }

  public void updateStopsList(ArrayList<StopPoint> busStops) {
    this.busStops = busStops;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public BusStopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(context);

    View busStopsView = inflater.inflate(R.layout.bus_stops_card, parent, false);

    BusStopsViewHolder viewHolder = new BusStopsViewHolder(busStopsView);

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull BusStopsViewHolder holder, final int position) {
    holder.busStopNameTextView.setText(busStops.get(position).getStopName());
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(context, BusDeparturesActivity.class);
        intent.putExtra(MainActivity.EXTRA_ACTIVITYNAME, BusStopSearchActivity.TAG);
        intent.putExtra(BusStopSearchActivity.EXTRA_CODE, busStops.get(position).getCode());
        intent.putExtra(BusStopSearchActivity.EXTRA_STOPID, busStops.get(position).getStopId());
        intent.putExtra(BusStopSearchActivity.EXTRA_STOPNAME, busStops.get(position).getStopName());
        context.startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() {
    return busStops.size();
  }
}
