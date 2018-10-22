package com.example.junyoung.uiucbus.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.uiucbus.BusDeparturesActivity;
import com.example.junyoung.uiucbus.BusRoutesActivity;
// import com.example.junyoung.uiucbus.BusStopsInMapActivity;
import com.example.junyoung.uiucbus.DeviceDimensionsHelper;
import com.example.junyoung.uiucbus.MainActivity;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.httpclient.pojos.BusInfo;
import com.example.junyoung.uiucbus.httpclient.pojos.BusSchedules;
import com.example.junyoung.uiucbus.httpclient.pojos.NotifyItemData;
import com.example.junyoung.uiucbus.utils.TimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BusFavoriteDeparturesAdapter
  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  public static class BusStopBannerViewHolder extends RecyclerView.ViewHolder {
    public TextView busStopNameTextView;
    public TextView busStopCodeTextView;

    public BusStopBannerViewHolder(View itemView) {
      super(itemView);
      busStopNameTextView = itemView.findViewById(R.id.textview_bus_stop_name_expandable_card);
      busStopCodeTextView = itemView.findViewById(R.id.textview_bus_stop_code_expandable_card);
    }
  }

  private static final String TAG = "BusFavoriteAdapter";

  private Context context;
  private String activityName;
  private BusSchedules busSchedules;
  private ArrayList<Integer> headerPositions;

  public BusFavoriteDeparturesAdapter(Context context,
                                      String activityName,
                                      BusSchedules busSchedules) {
    this.context = context;
    this.activityName = activityName;
    this.busSchedules = busSchedules;
    this.headerPositions = new ArrayList<>();
  }

  public void updateBusSchedules(BusSchedules busSchedules, ArrayList<Integer> headerPosition) {
    this.busSchedules = busSchedules;
    Log.d(TAG, busSchedules.getBusNameList().toString());
    this.headerPositions.clear();
    this.headerPositions.addAll(headerPosition);
    notifyDataSetChanged();
  }

  public void updateBusData(NotifyItemData itemData) {
    this.busSchedules = itemData.getBusSchedules();
    if (itemData.getNotifyDataChangedList() != null) {
      for (int index : itemData.getNotifyDataChangedList()) {
        notifyItemChanged(index);
      }
    }
    if (itemData.getNotifyDataInsertedList() != null) {
      for (int index : itemData.getNotifyDataInsertedList()) {
        notifyItemInserted(index);
      }
    }
  }


  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View busDeparturesView = inflater.inflate(R.layout.bus_departures_card, parent, false);
    switch (viewType) {
      case 0:
        View busStopBannerView = inflater.inflate(R.layout.bus_stop_banner_card, parent, false);
        return new BusStopBannerViewHolder(busStopBannerView);
      case 1:
        //return new BusDeparturesAdapter.BusDeparturesViewHolder(busDeparturesView);
    }

    return null;//new BusDeparturesAdapter.BusDeparturesViewHolder(busDeparturesView);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    switch (holder.getItemViewType()) {
      case 0:
        BusStopBannerViewHolder busStopBannerVH = (BusStopBannerViewHolder) holder;
        String busStopName = busSchedules.getBusNameList().get(position);
        String busStopCode = busSchedules.getBusInfoList().get(position).getStopCode();
        busStopBannerVH.busStopNameTextView.setText(busStopName);
        busStopBannerVH.busStopCodeTextView.setText(busStopCode);
        if (busSchedules.getBusInfoList().size() == headerPositions.size() ||
          busSchedules.getBusNameList().size() == (position + 1)) {
          busStopBannerVH.itemView.setBackground(
            ContextCompat.getDrawable(context, R.drawable.rounded_corner));
          ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView
            .getLayoutParams();
          int pixel = (int) DeviceDimensionsHelper.convertDpToPixel(16, context);
          int topMargin = (int) DeviceDimensionsHelper.convertDpToPixel(8, context);
          params.leftMargin = pixel;
          params.rightMargin = pixel;
          params.topMargin = topMargin;
          params.bottomMargin = pixel;
        }
        break;
      case 1:
        Log.d("Position: ", String.valueOf(position));
        final BusDeparturesAdapter.BusDeparturesViewHolder busDeparturesVH =
          (BusDeparturesAdapter.BusDeparturesViewHolder) holder;
        final String cardBusNameText;
        final String cardBackgroundColor;
        String cardBusEstimatedArrivalTime = null;
        String cardSecondBusEstimatedArrivalTime = null;

        setLayoutMargin(busDeparturesVH);

        if (position == (busSchedules.getBusNameList().size() - 1)
          || headerPositions.contains(position + 1)) {
          busDeparturesVH.card.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corner_at_bottom));
        }

        if (busSchedules != null && !busSchedules.getBusNameList().isEmpty()) {
          BusInfo busInfo = busSchedules.getBusInfoList().get(position);
          cardBusNameText = busInfo.getHeadsign().split(",")[0];
          String cardDirectionText = "To " + busInfo.getTripHeadSign();
          cardBackgroundColor = "#" + busInfo.getRouteColor();
          String cardBusNameTextColor = "#" + busInfo.getRouteTextColor();
          String cardDirectionTextColor = "#" + busInfo.getRouteTextColor();


          // Check if there is valid expected bus departure time
          ArrayList<String> expectedTime = busInfo.getExpected();
          if (!expectedTime.isEmpty()) {
            String firstExpectedTime = expectedTime.get(0);
            if (firstExpectedTime != null) {
              cardBusEstimatedArrivalTime = firstExpectedTime;
            }
            if (expectedTime.size() > 1) {
              String secondExpectedTime = expectedTime.get(1);
              if (secondExpectedTime != null) {
                cardSecondBusEstimatedArrivalTime = secondExpectedTime;
              }
            }
          }

          long timeDiffFirst =
            TimeFormatter.getRemainingArrivalTime(cardBusEstimatedArrivalTime) + 20000;

          if (busDeparturesVH.timer != null) {
            busDeparturesVH.timer.cancel();
            busDeparturesVH.timer = null;
          }
          if (busDeparturesVH.timer2 != null) {
            busDeparturesVH.timer2.cancel();
            busDeparturesVH.timer2 = null;
          }

          busDeparturesVH.timer = new CountDownTimer(timeDiffFirst, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
              changeTextTimerByTime(true, millisUntilFinished, busDeparturesVH);
            }

            @Override
            public void onFinish() {
              busSchedules.getBusInfoList().get(busDeparturesVH.getAdapterPosition())
                .deleteBusInfo(0);
              notifyItemChanged(busDeparturesVH.getAdapterPosition());
            }
          }.start();

          if (cardSecondBusEstimatedArrivalTime != null) {
            long timeDiffSecond =
              TimeFormatter.getRemainingArrivalTime(cardSecondBusEstimatedArrivalTime) + 20000;

            busDeparturesVH.timer2 = new CountDownTimer(timeDiffSecond, 1000) {
              @Override
              public void onTick(long millisUntilFinished) {
                changeTextTimerByTime(false, millisUntilFinished, busDeparturesVH);
              }

              @Override
              public void onFinish() {
                busSchedules.getBusInfoList().get(busDeparturesVH.getAdapterPosition())
                  .deleteBusInfo(1);
                notifyItemChanged(busDeparturesVH.getAdapterPosition());
              }
            }.start();
          } else {
            //busDeparturesVH.minutes2TextView.setVisibility(View.INVISIBLE);
            //busDeparturesVH.busArrivalRemainingTimeSeconds2TextView.setVisibility(View.INVISIBLE);
            //busDeparturesVH.busArrivalRemainingTimeMinutes2TextView.setVisibility(View.INVISIBLE);
            //busDeparturesVH.busArrivalRemainingTimeStatus2TextView.setText(R.string.no_info);
          }

          //busDeparturesVH.busDirectionTextView.setText(cardDirectionText);
          //busDeparturesVH.busNameTextView.setText(cardBusNameText);

          /*
          busDeparturesVH.card.setBackgroundTintList(
            ColorStateList.valueOf(Color.parseColor(cardBackgroundColor)));
          busDeparturesVH.minutesTextView.setTextColor(Color.parseColor(cardBusNameTextColor));
          busDeparturesVH.minutes2TextView.setTextColor(Color.parseColor(cardBusNameTextColor));
          busDeparturesVH.busDirectionTextView.setTextColor(Color.parseColor(cardDirectionTextColor));
          busDeparturesVH.busNameTextView.setTextColor(Color.parseColor(cardBusNameTextColor));
          busDeparturesVH.busArrivalRemainingTimeStatusTextView.setTextColor(Color.parseColor(cardBusNameTextColor));
          busDeparturesVH.busArrivalRemainingTimeMinutesTextView.setTextColor(Color.parseColor(cardBusNameTextColor));
          busDeparturesVH.busArrivalRemainingTimeSecondsTextView.setTextColor(Color.parseColor(cardBusNameTextColor));
          busDeparturesVH.busArrivalRemainingTimeStatus2TextView.setTextColor(Color.parseColor
            (cardBusNameTextColor));
          busDeparturesVH.busArrivalRemainingTimeMinutes2TextView.setTextColor(Color.parseColor
            (cardBusNameTextColor));
          busDeparturesVH.busArrivalRemainingTimeSeconds2TextView.setTextColor(Color.parseColor
            (cardBusNameTextColor));
            */
          //busDeparturesVH.busNameTextView.setTextColor(Color.parseColor(cardBackgroundColor));

          holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, BusRoutesActivity.class);
            intent.putStringArrayListExtra(
              BusDeparturesActivity.EXTRA_TRIPID,
              busSchedules.getBusInfoList().get(busDeparturesVH.getAdapterPosition()).getTripId()
            );
            intent.putStringArrayListExtra(
              BusDeparturesActivity.EXTRA_SHAPEID,
              busSchedules.getBusInfoList().get(busDeparturesVH.getAdapterPosition()).getShapeId()
            );
            intent.putStringArrayListExtra(
              BusDeparturesActivity.EXTRA_VEHICLEID,
              busSchedules.getBusInfoList().get(busDeparturesVH.getAdapterPosition()).getVehicleId()
            );
            intent.putExtra(MainActivity.EXTRA_ACTIVITYNAME, activityName);
            intent.putExtra(BusDeparturesActivity.EXTRA_BUSNAME, cardBusNameText);
            intent.putExtra(BusDeparturesActivity.EXTRA_BUSCOLOR, cardBackgroundColor);
            String[] busNameandBusStopName = busSchedules.getBusNameList().get(busDeparturesVH
              .getAdapterPosition()).split(",");
            // intent.putExtra(BusStopsInMapActivity.EXTRA_STOPNAME, busNameandBusStopName[1]);
            Log.d(TAG, busSchedules.getBusNameList().get(busDeparturesVH.getAdapterPosition()));
            context.startActivity(intent);
          });
        }
        break;
    }
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    Log.d(TAG, "GET OLD POSITION: " + holder.getOldPosition() + " LAYOUT POSITION: " +
      holder.getLayoutPosition() + " POSITION: " + holder.getAdapterPosition());
    if (!headerPositions.contains(holder.getAdapterPosition())) {
      BusDeparturesAdapter.BusDeparturesViewHolder vh =
        (BusDeparturesAdapter.BusDeparturesViewHolder) holder;
      if (vh.timer != null) {
        vh.timer.cancel();
        vh.timer = null;
      }
      if (vh.timer2 != null) {
        vh.timer2.cancel();
        vh.timer2 = null;
      }
      Log.d(TAG, "Timers are canceled");
    }
  }

  @Override
  public int getItemCount() {
    if (busSchedules.getBusNameList() != null) {
      return busSchedules.getBusNameList().size();
    } else {
      return 0;
    }
  }

  @Override
  public int getItemViewType(int position) {
    for (int headerPosition : headerPositions) {
      if (headerPosition == position) {
        return 0;
      }
    }

    return 1;
  }

  public BusSchedules getBusSchedules() {
    return this.busSchedules;
  }

  private void setLayoutMargin(BusDeparturesAdapter.BusDeparturesViewHolder busDeparturesVH) {
    final float scale = context.getResources().getDisplayMetrics().density;
    int pixels = (int) (16 * scale + 0.5f);
    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    );
    layoutParams.setMargins(pixels, 0, pixels, 0);
    busDeparturesVH.card.setLayoutParams(layoutParams);
  }

  private long calculateTimeDifference(String cardBusEstimatedArrivalTime) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
    dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));

    Date estimatedArrivalTime = null;

    try {
      estimatedArrivalTime = dateFormat.parse(cardBusEstimatedArrivalTime);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return estimatedArrivalTime.getTime() - new Date().getTime();
  }


  private void changeTextTimerByTime(boolean isFirstTimer,
                                     long millisUntilFinished,
                                     BusDeparturesAdapter.BusDeparturesViewHolder vh) {
    if (isFirstTimer) {
      if (millisUntilFinished > 80000) {
        /*
        long seconds = (millisUntilFinished - 20000) / 1000 % 60;
        long minutes = (millisUntilFinished - 20000) / (60 * 1000) % 60;
        vh.minutesTextView.setVisibility(View.VISIBLE);
        vh.busArrivalRemainingTimeSecondsTextView.setVisibility(View.VISIBLE);
        vh.busArrivalRemainingTimeMinutesTextView.setVisibility(View.VISIBLE);
        vh.busArrivalRemainingTimeStatusTextView.setText("s");
        vh.busArrivalRemainingTimeSecondsTextView.setText(String.valueOf(seconds));
        vh.busArrivalRemainingTimeMinutesTextView.setText(String.valueOf(minutes));
      } else if (20000 <= millisUntilFinished && millisUntilFinished <= 80000) {
        vh.minutesTextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeSecondsTextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeMinutesTextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeStatusTextView.setText(R.string.bus_arrives_soon);
      } else if (millisUntilFinished <= 20000) {
        vh.minutesTextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeSecondsTextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeMinutesTextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeStatusTextView.setText(R.string.bus_arrived);
      }
    } else {
      if (millisUntilFinished > 80000) {
        long seconds = (millisUntilFinished - 20000) / 1000 % 60;
        long minutes = (millisUntilFinished - 20000)/ (60 * 1000) % 60;
        vh.minutes2TextView.setVisibility(View.VISIBLE);
        vh.busArrivalRemainingTimeSeconds2TextView.setVisibility(View.VISIBLE);
        vh.busArrivalRemainingTimeMinutes2TextView.setVisibility(View.VISIBLE);
        vh.busArrivalRemainingTimeStatus2TextView.setText("s");
        vh.busArrivalRemainingTimeSeconds2TextView.setText(String.valueOf(seconds));
        vh.busArrivalRemainingTimeMinutes2TextView.setText(String.valueOf(minutes));
      } else if (20000 <= millisUntilFinished && millisUntilFinished <= 80000) {
        vh.minutesTextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeSeconds2TextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeMinutes2TextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeStatus2TextView.setText(R.string.bus_arrives_soon);
      } else if (millisUntilFinished <= 20000) {
        vh.minutes2TextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeSeconds2TextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeMinutes2TextView.setVisibility(View.INVISIBLE);
        vh.busArrivalRemainingTimeStatus2TextView.setText(R.string.bus_arrived);
      }
      */
      }

    }
  }
}
