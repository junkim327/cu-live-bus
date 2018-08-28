package com.example.junyoung.uiucbus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.uiucbus.httpclient.pojos.StopTimes;

import java.util.ArrayList;

public class BusRoutesAdapter extends Adapter<BusRoutesAdapter.BusRoutesViewHolder> {
  private Context context;
  private ArrayList<StopTimes> stopTimesList;

  public static class BusRoutesViewHolder extends ViewHolder {
    public TextView busStopCodeTextView;
    public TextView busStopNameTextView;

    public BusRoutesViewHolder(View itemView) {
      super(itemView);

      busStopCodeTextView = itemView.findViewById(R.id.textview_code_bus_routes);
      busStopNameTextView = itemView.findViewById(R.id.textview_bus_stop_name_bus_routes_card);
    }
  }

  public BusRoutesAdapter(Context context, ArrayList<StopTimes> stopTimesList) {
    this.context = context;
    this.stopTimesList = stopTimesList;
  }

  public void updateStopTimesList(ArrayList<StopTimes> stopTimesList) {
    this.stopTimesList = stopTimesList;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public BusRoutesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(context);

    View busRoutesView = inflater.inflate(R.layout.bus_routes_card, parent, false);

    BusRoutesViewHolder viewHolder = new BusRoutesViewHolder(busRoutesView);

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull BusRoutesViewHolder holder, int position) {
    holder.busStopCodeTextView.setText(stopTimesList.get(position).getStopPoint().getCode());
    holder.busStopNameTextView.setText(stopTimesList.get(position).getStopPoint().getStopName());
  }

  @Override
  public int getItemCount() {
    return stopTimesList.size();
  }
}
