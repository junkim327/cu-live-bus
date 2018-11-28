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

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusDeparturesAdapter extends RecyclerView.Adapter<BusDeparturesAdapter.BusDeparturesViewHolder> {
  private static final long MAGIC_NUMBER = 10000L;

  private Context mContext;
  private LayoutInflater mInflater;
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
    Log.d("BusDeparturesAdapter", "onCreateViewHolder is called");
    if (mInflater == null) {
      mInflater = LayoutInflater.from(mContext);
    }

    View view = mInflater.inflate(R.layout.card_bus_departures, parent, false);
    mViewHolder = new BusDeparturesViewHolder(view, mRecyclerViewClickListener);

    return mViewHolder;
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

      holder.mTimer = new CountDownTimer(firstBusRemainingArrivalTime, DateUtils.SECOND_IN_MILLIS) {
        @Override
        public void onTick(long remainingMillis) {
          //Log.d("Timer", "Clock is ticking");
          if (remainingMillis > DateUtils.MINUTE_IN_MILLIS + MAGIC_NUMBER) {
            int minutes = (int) (remainingMillis - MAGIC_NUMBER) / (60 * 1000) % 60;
            int seconds = (int) (remainingMillis - MAGIC_NUMBER) / 1000 % 60;
            holder.mTicker1.setText(mContext.getString(
              R.string.bus_remaining_arrival_time,
              minutes,
              seconds
            ));
          } else if (remainingMillis > MAGIC_NUMBER) {
            holder.mTicker1.setText(mContext.getString(
              R.string.bus_arrives_soon
            ));
          } else {
            holder.mTicker1.setText(mContext.getString(
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

        holder.mTimer2 = new CountDownTimer(secondBusRemainingArrivalTime,
          DateUtils.SECOND_IN_MILLIS) {
          @Override
          public void onTick(long remainingMillis) {
            //Log.d("Timer2", "Clock is ticking");
            if (remainingMillis > DateUtils.MINUTE_IN_MILLIS + MAGIC_NUMBER) {
              int minutes = (int) (remainingMillis - MAGIC_NUMBER) / (60 * 1000) % 60;
              int seconds = (int) (remainingMillis - MAGIC_NUMBER) / 1000 % 60;
              holder.mTicker2.setText(mContext.getString(
                R.string.bus_remaining_arrival_time,
                minutes,
                seconds
              ));
            } else if (remainingMillis > MAGIC_NUMBER) {
              holder.mTicker2.setText(mContext.getString(
                R.string.bus_arrives_soon
              ));
            } else {
              holder.mTicker2.setText(mContext.getString(
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
      if (holder.mTimer != null) {
        holder.mTimer.cancel();
        holder.mTimer = null;
      }
      if (holder.mTimer2 != null) {
        holder.mTimer2.cancel();
        holder.mTimer2 = null;
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

    if (holder.mTimer != null) {
      holder.mTimer.cancel();
      holder.mTimer = null;
    }
    if (holder.mTimer2 != null) {
      holder.mTimer2.cancel();
      holder.mTimer2 = null;
    }
  }

  @Override
  public int getItemCount() {
    return mSortedDepartureList.size();
  }

  static class BusDeparturesViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
    public ConstraintLayout mCard;
    public CountDownTimer mTimer, mTimer2;
    public RecyclerviewClickListener mRecyclerViewClickListener;

    @BindView(R.id.text_bus_name_card_bus_departures)
    TextView mBusNameTextView;
    @BindView(R.id.text_direction_card_bus_departures)
    TextView mBusDirectionTextView;
    @BindView(R.id.ticker_one_card_bus_departures)
    TextView mTicker1;
    @BindView(R.id.ticker_two_card_bus_departures)
    TextView mTicker2;

    public BusDeparturesViewHolder(View itemView, RecyclerviewClickListener listener) {
      super(itemView);

      ButterKnife.bind(this, itemView);

      mCard = (ConstraintLayout) itemView;
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
}