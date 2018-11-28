package com.example.junyoung.culivebus.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;
import com.example.junyoung.culivebus.httpclient.pojos.FavoriteBusDepartures;
import com.example.junyoung.culivebus.httpclient.pojos.SortedDeparture;
import com.example.junyoung.culivebus.room.entity.UserSavedBusStop;
import com.example.junyoung.culivebus.adapter.BusDeparturesAdapter.BusDeparturesViewHolder;
import com.example.junyoung.culivebus.util.TimeFormatter;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusFavoriteDeparturesAdapter
  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  public static class BusStopHeaderViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout card;
    @BindView(R.id.textview_bus_stop_name_bus_stop_header)
    TextView busStopNameTextView;
    @BindView(R.id.textview_bus_stop_code_bus_stop_header)
    TextView busStopCodeTextView;

    public BusStopHeaderViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);

      card = (ConstraintLayout) itemView;
    }
  }

  private static final String TAG = BusFavoriteDeparturesAdapter.class.getSimpleName();
  private static final int HEADER = 0;
  private static final int DEPARTURE = 1;
  private static final long MAGIC_NUMBER = 10000L;

  private Context mContext;
  private List<FavoriteBusDepartures> mDepartureList = Collections.emptyList();
  private RecyclerviewClickListener mDepartureClickListener;

  public BusFavoriteDeparturesAdapter(Context context,
                                      RecyclerviewClickListener departureClickListener) {
    mContext = context;
    mDepartureClickListener = departureClickListener;
  }

  public void updateBusDepartures(List<FavoriteBusDepartures> departureList) {
    mDepartureList = departureList;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder;
    LayoutInflater inflater = LayoutInflater.from(mContext);

    switch (viewType) {
      case HEADER:
        View busStopBannerView = inflater.inflate(R.layout.card_bus_stop_header, parent, false);
        viewHolder = new BusStopHeaderViewHolder(busStopBannerView);
        break;
      default:
        View busDepartureView = inflater.inflate(R.layout.card_bus_departures, parent, false);
        viewHolder = new BusDeparturesAdapter.BusDeparturesViewHolder(busDepartureView,
          mDepartureClickListener);
        break;
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    //Log.d(TAG, "onBindViewHolder has called at position : " + holder.getAdapterPosition());
    switch (holder.getItemViewType()) {
      case HEADER:
        BusStopHeaderViewHolder headerViewHolder = (BusStopHeaderViewHolder) holder;
        configureHeaderViewHolder(headerViewHolder, position);
          /*
          if (busSchedules.getBusInfoList().size() == headerPositions.size() ||
            busSchedules.getBusNameList().size() == (position + 1)) {
            busStopBannerVH.itemView.setBackground(
              ContextCompat.getDrawable(mContext, R.drawable.rounded_corner));
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView
              .getLayoutParams();
            int pixel = (int) DeviceDimensionsHelper.convertDpToPixel(16, mContext);
            int topMargin = (int) DeviceDimensionsHelper.convertDpToPixel(8, mContext);
            params.leftMargin = pixel;
            params.rightMargin = pixel;
            params.topMargin = topMargin;
            params.bottomMargin = pixel;
          } */
        break;
      case DEPARTURE:
        BusDeparturesViewHolder departuresViewHolder = (BusDeparturesViewHolder) holder;
        configureDepartureViewHolder(departuresViewHolder, position);

        break;
    }
  }

  private void configureHeaderViewHolder(BusStopHeaderViewHolder holder, int position) {
    setHeaderCardBackground(holder, position);

    UserSavedBusStop userSavedBusStop = mDepartureList.get(position).getBusStop();
    if (userSavedBusStop != null) {
      holder.busStopNameTextView.setText(userSavedBusStop.getSavedStopName());
      holder.busStopCodeTextView.setText(userSavedBusStop.getSavedStopCode());
    }
  }

  private void configureDepartureViewHolder(BusDeparturesViewHolder holder, int position) {
    holder.setIsRecyclable(false);

    // Change card view
    setLayoutMargin(holder);
    setDepartureCardBackground(holder, position);

    SortedDeparture departure = mDepartureList.get(position).getDeparture();
    if (departure != null) {
      holder.mBusNameTextView.setText(departure.getHeadSign());
      holder.mBusDirectionTextView.setText(
        mContext.getString(
          R.string.bus_direction_info,
          departure.getTripList().get(0).getTripHeadSign()
        )
      );

      long firstBusRemainingArrivalTime = TimeFormatter.getRemainingArrivalTime(
        departure.getExpectedList().get(0)) + MAGIC_NUMBER;

      if (holder.mTimer != null) {
        holder.mTimer.cancel();
        holder.mTimer = null;
      }
      holder.mTimer = new CountDownTimer(firstBusRemainingArrivalTime, DateUtils.SECOND_IN_MILLIS) {
        @Override
        public void onTick(long remainingMillis) {
          //Log.d("Timer", "Clock is ticking");
          if (remainingMillis > DateUtils.MINUTE_IN_MILLIS + MAGIC_NUMBER) {
            int minutes = (int) (remainingMillis - MAGIC_NUMBER) / (60 * 1000) % 60;
            int seconds = (int) (remainingMillis - MAGIC_NUMBER) / 1000 % 60;
            if (seconds % 5 == 0) {
              Log.d("Timer1", "Clock is ticking, possible memory leak");
            }
            holder.mTicker1.setText(mContext.getString(
              R.string.bus_remaining_arrival_time,
              minutes,
              seconds
            ));
          } else if (remainingMillis > MAGIC_NUMBER) {
            holder.mTicker1.setText(mContext.getString(R.string.bus_arrives_soon));
          } else {
            holder.mTicker1.setText(mContext.getString(R.string.bus_arrived));
          }
        }

        @Override
        public void onFinish() {

        }
      }.start();

      if (departure.getExpectedList().size() > 1) {
        long secondBusRemainingArrivalTime = TimeFormatter.getRemainingArrivalTime(
          departure.getExpectedList().get(1)) + MAGIC_NUMBER;

        if (holder.mTimer2 != null) {
          holder.mTimer2.cancel();
          holder.mTimer2 = null;
        }
        holder.mTimer2 = new CountDownTimer(secondBusRemainingArrivalTime,
          DateUtils.SECOND_IN_MILLIS) {
          @Override
          public void onTick(long remainingMillis) {
            //Log.d("Timer2", "Clock is ticking");
            if (remainingMillis > DateUtils.MINUTE_IN_MILLIS + MAGIC_NUMBER) {
              int minutes = (int) (remainingMillis - MAGIC_NUMBER) / (60 * 1000) % 60;
              int seconds = (int) (remainingMillis - MAGIC_NUMBER) / 1000 % 60;
              if (seconds % 5 == 0) {
                //Log.d("Timer2", "Clock is ticking, possible memory leak");
              }
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

  @Override
  public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
    super.onViewRecycled(holder);
    Log.d(TAG, "onViewRecycled has called at position: " + holder.getAdapterPosition());
  }

  @Override
  public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    Log.d(TAG, "onDetachedFromRecyclerView has called.");
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    if (holder instanceof BusDeparturesViewHolder) {
      Log.d(TAG, "Departure View Detached From Window at old position : ");
      ((BusDeparturesViewHolder) holder).mTimer.cancel();
      ((BusDeparturesViewHolder) holder).mTimer = null;
      if (((BusDeparturesViewHolder) holder).mTimer2 != null) {
        ((BusDeparturesViewHolder) holder).mTimer2.cancel();
        ((BusDeparturesViewHolder) holder).mTimer2 = null;
      }
    } else {
      Log.d(TAG, "View Detached From Window at position : " + holder.getAdapterPosition());
    }
    //Log.d(TAG, "View Detached From Window at position : " + holder.getAdapterPosition());
  }

  @Override
  public int getItemCount() {
    return mDepartureList != null ? mDepartureList.size() : 0;
  }

  @Override
  public int getItemViewType(int position) {
    if (mDepartureList.get(position).isBusDeparture()) {
      return DEPARTURE;
    } else {
      return HEADER;
    }
  }

  private void setLayoutMargin(BusDeparturesViewHolder busDeparturesVH) {
    final float scale = mContext.getResources().getDisplayMetrics().density;
    int pixels = (int) (16 * scale + 0.5f);
    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    );
    layoutParams.setMargins(pixels, 0, pixels, 0);
    busDeparturesVH.mCard.setLayoutParams(layoutParams);
  }

  private void setHeaderCardBackground(BusStopHeaderViewHolder holder, int position) {
    // TODO : Implement logic
    if (((position + 1) == mDepartureList.size()) || !mDepartureList.get(position + 1)
      .isBusDeparture())
      holder.card.setBackground(ResourcesCompat.getDrawable(mContext.getResources(),
        R.drawable.rounded_corner, null));
  }

  private void setDepartureCardBackground(BusDeparturesViewHolder holder, int position) {
    if ((position + 1) == mDepartureList.size() || !mDepartureList.get(position + 1)
      .isBusDeparture()) {
      holder.mCard.setBackground(ResourcesCompat.getDrawable(mContext.getResources(),
        R.drawable.rounded_corner_at_bottom, null));
    }
  }
}