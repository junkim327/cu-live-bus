package com.example.junyoung.culivebus.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;
import com.example.junyoung.culivebus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.culivebus.util.TimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class BusDeparturesAdapter extends RecyclerView.Adapter<BusDeparturesAdapter.BusDeparturesViewHolder> {
  public static class BusDeparturesViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
    public CountDownTimer timer;
    public CountDownTimer timer2;
    public ConstraintLayout card;
    public TextView mBusNameTextView;
    public TextView mBusDirectionTextView;
    public TextView mOneBusEstimatedArrivalTimeTextView;
    public TextView mTwoBusEstimatedArrivalTimeTextView;
    public RecyclerviewClickListener mRecyclerViewClickListener;

    public BusDeparturesViewHolder(View itemView, RecyclerviewClickListener listener) {
      super(itemView);

      card = (ConstraintLayout) itemView;
      mBusNameTextView = itemView.findViewById(R.id.text_bus_name_card_bus_departures);
      mBusDirectionTextView = itemView.findViewById(R.id.text_direction_card_bus_departures);
      mOneBusEstimatedArrivalTimeTextView =
        itemView.findViewById(R.id.text_one_estimated_arrival_time_card_bus_departures);
      mTwoBusEstimatedArrivalTimeTextView =
        itemView.findViewById(R.id.text_two_estimated_arrival_time_card_bus_departures);
      mRecyclerViewClickListener = listener;
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      int pos = getAdapterPosition();
      if (pos != RecyclerView.NO_POSITION) {
        mRecyclerViewClickListener.onClick(view, pos);
      }
    }
  }

  private static final long MAGIC_NUMBER = 10000L;

  private Context mContext;
  private BusDeparturesViewHolder mViewHolder;
  private List<SortedDeparture> mSortedDepartureList = new ArrayList<>();
  private RecyclerviewClickListener mRecyclerViewClickListener;

  public BusDeparturesAdapter(Context context, RecyclerviewClickListener listener) {
    mContext = context;
    mRecyclerViewClickListener = listener;
  }

  public void updateDepartureList(List<SortedDeparture> sortedDepartureList) {
    mSortedDepartureList.clear();
    mSortedDepartureList.addAll(sortedDepartureList);

    Log.d("BusDeparturesAdapter", "Update recyclerview.");
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public BusDeparturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);

    Log.d("BusDeparturesAdapter", "onCreateViewHolder is called");

    View view = inflater.inflate(R.layout.card_bus_departures, parent, false);
    BusDeparturesViewHolder viewHolder = new BusDeparturesViewHolder(
      view,
      mRecyclerViewClickListener
    );
    mViewHolder = viewHolder;

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull final BusDeparturesViewHolder holder, int position) {
    if (mSortedDepartureList.size() != 0) {
      Log.d("BusDeparturesAdapter", "onBindViewHolder is called");
      SortedDeparture departure = mSortedDepartureList.get(position);

      holder.mBusNameTextView.setText(departure.getHeadSign());
      holder.mBusDirectionTextView.setText(
        mContext.getString(
          R.string.bus_direction_info,
          departure.getTripList().get(0).getTripHeadSign()
        )
      );

      long firstBusRemainingArrivalTime = TimeFormatter.getRemainingArrivalTime(
        departure.getExpectedList().get(0)) + MAGIC_NUMBER;

      holder.timer = new CountDownTimer(firstBusRemainingArrivalTime, DateUtils.SECOND_IN_MILLIS) {
        @Override
        public void onTick(long remainingMillis) {
          //Log.d("Timer", "Clock is ticking");
          if (remainingMillis > DateUtils.MINUTE_IN_MILLIS + MAGIC_NUMBER) {
            int minutes = (int) (remainingMillis - MAGIC_NUMBER) / (60 * 1000) % 60;
            int seconds = (int) (remainingMillis - MAGIC_NUMBER) / 1000 % 60;
            holder.mOneBusEstimatedArrivalTimeTextView.setText(mContext.getString(
              R.string.bus_remaining_arrival_time,
              minutes,
              seconds
              ));
          } else if (remainingMillis > MAGIC_NUMBER) {
            holder.mOneBusEstimatedArrivalTimeTextView.setText(mContext.getString(
              R.string.bus_arrives_soon
            ));
          } else {
            holder.mOneBusEstimatedArrivalTimeTextView.setText(mContext.getString(
              R.string.bus_arrived
            ));
          }
        }

        @Override
        public void onFinish() {

        }
      }.start();

      if (departure.getExpectedList().size() > 1) {
        long secondBusRemainingArrivalTime = TimeFormatter.getRemainingArrivalTime(
          departure.getExpectedList().get(1)) + MAGIC_NUMBER;

        holder.timer2 = new CountDownTimer(secondBusRemainingArrivalTime,
          DateUtils.SECOND_IN_MILLIS) {
          @Override
          public void onTick(long remainingMillis) {
            //Log.d("Timer2", "Clock is ticking");
            if (remainingMillis > DateUtils.MINUTE_IN_MILLIS + MAGIC_NUMBER) {
              int minutes = (int) (remainingMillis - MAGIC_NUMBER) / (60 * 1000) % 60;
              int seconds = (int) (remainingMillis - MAGIC_NUMBER) / 1000 % 60;
              holder.mTwoBusEstimatedArrivalTimeTextView.setText(mContext.getString(
                R.string.bus_remaining_arrival_time,
                minutes,
                seconds
              ));
            } else if (remainingMillis > MAGIC_NUMBER) {
              holder.mTwoBusEstimatedArrivalTimeTextView.setText(mContext.getString(
                R.string.bus_arrives_soon
              ));
            } else {
              holder.mTwoBusEstimatedArrivalTimeTextView.setText(mContext.getString(
                R.string.bus_arrived
              ));
            }
          }

          @Override
          public void onFinish() {

          }
        }.start();
      }
    }
  }

  /**
   * Return the holder of the view. This method is necessary to call onViewDetachedFromWindow()
   * from the fragment.
   *
   * @return Holder of the view.
   */
  public BusDeparturesViewHolder getViewHolder() {
    return mViewHolder;
  }

  public void cancelTimer(RecyclerView.ViewHolder vh) {
    BusDeparturesViewHolder holder = (BusDeparturesViewHolder) vh;

    if (holder != null) {
      if (holder.timer != null) {
        holder.timer.cancel();
        holder.timer = null;
      }
      if (holder.timer2 != null) {
        holder.timer2.cancel();
        holder.timer2 = null;
      }
    }
  }

  /**
   * Called when a view created by this adapter has been detached from its window.
   * This method stops the timers to prevent the memory leak.
   *
   * @param holder Holder of the view being detached.
   */
  @Override
  public void onViewDetachedFromWindow(@NonNull BusDeparturesViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    Log.d("BusDeparturesAdapter", "onViewDetachedFromWindow has called");

    if (holder.timer != null) {
      holder.timer.cancel();
      holder.timer = null;
    }
    if (holder.timer2 != null) {
      holder.timer2.cancel();
      holder.timer2 = null;
    }
  }

  @Override
  public int getItemCount() {
    return mSortedDepartureList.size();
  }
}
