package com.example.junyoung.culivebus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;
import com.example.junyoung.culivebus.room.entity.StopPoint;

import java.util.ArrayList;
import java.util.List;

public class BusStopsAdapter extends RecyclerView.Adapter<BusStopsAdapter.BusStopsViewHolder> {
  public static class BusStopsViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
    public TextView mBusStopNameTextView;
    public RecyclerviewClickListener mRecyclerViewClickListener;

    public BusStopsViewHolder(View itemView, RecyclerviewClickListener listener) {
      super(itemView);

      mRecyclerViewClickListener = listener;
      itemView.setOnClickListener(this);

      mBusStopNameTextView = itemView.findViewById(R.id.textview_bus_stop_name_card_bus_stop);
    }

    @Override
    public void onClick(View view) {
      int pos = getAdapterPosition();
      if (pos != RecyclerView.NO_POSITION) {
        mRecyclerViewClickListener.onClick(view, pos);
      }
    }
  }

  private Context mContext;
  private List<StopPoint> mBusStopList = new ArrayList<>();
  private RecyclerviewClickListener mRecyclerViewClickListener;

  public BusStopsAdapter(Context context, RecyclerviewClickListener listener) {
    mContext = context;
    mRecyclerViewClickListener = listener;
  }

  public void updateStopsList(List<StopPoint> busStops) {
    mBusStopList.clear();
    mBusStopList.addAll(busStops);
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public BusStopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);

    View busStopsView = inflater.inflate(R.layout.card_bus_stop, parent, false);

    BusStopsViewHolder viewHolder = new BusStopsViewHolder(
      busStopsView,
      mRecyclerViewClickListener
    );

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull final BusStopsViewHolder holder, int position) {
    if (mBusStopList != null && mBusStopList.size() != 0) {
      holder.mBusStopNameTextView.setText(mBusStopList.get(position).getStopName());
    }
  }

  @Override
  public int getItemCount() {
    return mBusStopList != null ? mBusStopList.size() : 0;
  }
}
