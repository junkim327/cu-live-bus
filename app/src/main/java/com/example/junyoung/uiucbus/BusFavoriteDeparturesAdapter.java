package com.example.junyoung.uiucbus;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import com.example.junyoung.uiucbus.httpclient.pojos.BusInfo;
import com.example.junyoung.uiucbus.httpclient.pojos.BusSchedules;
import com.example.junyoung.uiucbus.httpclient.pojos.UserFavoriteStops;

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

  private Context context;
  private UserFavoriteStops userFavoriteStops;
  private BusSchedules busSchedules;
  private ArrayList<Integer> headerPositions;

  public BusFavoriteDeparturesAdapter(Context context,
                                      BusSchedules busSchedules,
                                      UserFavoriteStops userFavoriteStops) {
    this.context = context;
    this.busSchedules = busSchedules;
    this.userFavoriteStops = userFavoriteStops;
    this.headerPositions = new ArrayList<>();
  }

  public void updateBusSchedules(BusSchedules busSchedules, ArrayList<Integer> headerPosition) {
    this.busSchedules = busSchedules;
    this.headerPositions.clear();
    this.headerPositions.addAll(headerPosition);
    notifyDataSetChanged();
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
        return new BusDeparturesAdapter.BusDeparturesViewHolder(busDeparturesView);
    }

    return new BusDeparturesAdapter.BusDeparturesViewHolder(busDeparturesView);
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
        if (busSchedules.getBusInfoList().size() == headerPositions.size()) {
          busStopBannerVH.itemView.setBackground(
            ContextCompat.getDrawable(context, R.drawable.rounded_corner));
        }
        break;
      case 1:
        Log.d("Position: ", String.valueOf(position));
        final BusDeparturesAdapter.BusDeparturesViewHolder busDeparturesVH =
          (BusDeparturesAdapter.BusDeparturesViewHolder) holder;
        String cardDirectionText;
        final String cardBusNameText;
        final String cardBackgroundColor;
        String cardDirectionTextColor;
        String cardBusNameTextColor;
        String cardBusEstimatedArrivalTime = null;
        String cardSecondBusEstimatedArrivalTime = null;

        setLayoutMargin(busDeparturesVH);

        if (position == (busSchedules.getBusNameList().size() - 1)
        || headerPositions.contains(position + 1)) {
          busDeparturesVH.card.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corner_at_bottom));
      }

        if (busSchedules != null && !busSchedules.getBusNameList().isEmpty()) {
          BusInfo busInfo = busSchedules.getBusInfoList().get(position);
          cardBusNameText = busInfo.getHeadsign();
          cardDirectionText = "To " + busInfo.getTripHeadSign();
          cardBackgroundColor = "#" + busInfo.getRouteColor();
          cardBusNameTextColor = "#" + busInfo.getRouteTextColor();
          cardDirectionTextColor = "#" + busInfo.getRouteTextColor();

          ArrayList<String> expectedTime = busInfo.getExpected();
          if (!expectedTime.isEmpty()) {
            if (expectedTime.get(0) != null) {
              cardBusEstimatedArrivalTime = expectedTime.get(0);
            }
            if (expectedTime.size() > 1) {
              if (expectedTime.get(1) != null) {
                cardSecondBusEstimatedArrivalTime = expectedTime.get(1);
              }
            }
          }

          long timeDiffFirst = calculateTimeDifference(cardBusEstimatedArrivalTime);

      /*
      if (holder.timer != null) {
        holder.timer.cancel();
        holder.progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            holder.progressBar.setVisibility(View.INVISIBLE);
          }
        }, 2000);
      }*/

          busDeparturesVH.timer = new CountDownTimer(timeDiffFirst, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
              if (millisUntilFinished > 60000) {
                long seconds = millisUntilFinished / 1000 % 60;
                long minutes = millisUntilFinished / (60 * 1000) % 60;
                busDeparturesVH.minutesTextView.setVisibility(View.VISIBLE);
                busDeparturesVH.busArrivalRemainingTimeSecondsTextView.setVisibility(View.VISIBLE);
                busDeparturesVH.busArrivalRemainingTimeMinutesTextView.setVisibility(View.VISIBLE);
                busDeparturesVH.busArrivalRemainingTimeStatusTextView.setText("s");
                busDeparturesVH.busArrivalRemainingTimeSecondsTextView.setText(String.valueOf(seconds));
                busDeparturesVH.busArrivalRemainingTimeMinutesTextView.setText(String.valueOf(minutes));
              } else if (millisUntilFinished <= 60000) {
                busDeparturesVH.minutesTextView.setVisibility(View.INVISIBLE);
                busDeparturesVH.busArrivalRemainingTimeSecondsTextView.setVisibility(View.INVISIBLE);
                busDeparturesVH.busArrivalRemainingTimeMinutesTextView.setVisibility(View.INVISIBLE);
                busDeparturesVH.busArrivalRemainingTimeStatusTextView.setText(R.string.bus_arrives_soon);
              }
            }

            @Override
            public void onFinish() {
              busDeparturesVH.minutesTextView.setVisibility(View.INVISIBLE);
              busDeparturesVH.busArrivalRemainingTimeSecondsTextView.setVisibility(View.INVISIBLE);
              busDeparturesVH.busArrivalRemainingTimeMinutesTextView.setVisibility(View.INVISIBLE);
              busDeparturesVH.busArrivalRemainingTimeStatusTextView.setText(R.string.bus_arrived);
            }
          }.start();

          if (cardSecondBusEstimatedArrivalTime != null) {
            long timeDiffSecond = calculateTimeDifference(cardSecondBusEstimatedArrivalTime);

            busDeparturesVH.timer2 = new CountDownTimer(timeDiffSecond, 1000) {
              @Override
              public void onTick(long millisUntilFinished) {
                if (millisUntilFinished > 60000) {
                  long seconds = millisUntilFinished / 1000 % 60;
                  long minutes = millisUntilFinished / (60 * 1000) % 60;
                  busDeparturesVH.minutes2TextView.setVisibility(View.VISIBLE);
                  busDeparturesVH.busArrivalRemainingTimeSeconds2TextView.setVisibility(View.VISIBLE);
                  busDeparturesVH.busArrivalRemainingTimeMinutes2TextView.setVisibility(View.VISIBLE);
                  busDeparturesVH.busArrivalRemainingTimeStatus2TextView.setText("s");
                  busDeparturesVH.busArrivalRemainingTimeSeconds2TextView.setText(String.valueOf(seconds));
                  busDeparturesVH.busArrivalRemainingTimeMinutes2TextView.setText(String.valueOf(minutes));
                } else if (millisUntilFinished <= 60000) {
                  busDeparturesVH.minutesTextView.setVisibility(View.INVISIBLE);
                  busDeparturesVH.busArrivalRemainingTimeSeconds2TextView.setVisibility(View.INVISIBLE);
                  busDeparturesVH.busArrivalRemainingTimeMinutes2TextView.setVisibility(View.INVISIBLE);
                  busDeparturesVH.busArrivalRemainingTimeStatus2TextView.setText(R.string.bus_arrives_soon);
                }
              }

              @Override
              public void onFinish() {
                busDeparturesVH.minutes2TextView.setVisibility(View.INVISIBLE);
                busDeparturesVH.busArrivalRemainingTimeSeconds2TextView.setVisibility(View.INVISIBLE);
                busDeparturesVH.busArrivalRemainingTimeMinutes2TextView.setVisibility(View.INVISIBLE);
                busDeparturesVH.busArrivalRemainingTimeStatus2TextView.setText(R.string.bus_arrived);
              }
            }.start();
          } else {
            busDeparturesVH.minutes2TextView.setVisibility(View.INVISIBLE);
            busDeparturesVH.busArrivalRemainingTimeSeconds2TextView.setVisibility(View.INVISIBLE);
            busDeparturesVH.busArrivalRemainingTimeMinutes2TextView.setVisibility(View.INVISIBLE);
            busDeparturesVH.busArrivalRemainingTimeStatus2TextView.setText(R.string.no_info);
          }

          busDeparturesVH.busDirectionTextView.setText(cardDirectionText);
          busDeparturesVH.busNameTextView.setText(cardBusNameText);

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

          holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
              intent.putExtra(BusDeparturesActivity.EXTRA_BUSNAME, cardBusNameText);
              intent.putExtra(BusDeparturesActivity.EXTRA_BUSCOLOR, cardBackgroundColor);
              intent.putExtra(BusStopsInMapActivity.EXTRA_STOPNAME, busSchedules.getBusNameList()
                .get(0));
              context.startActivity(intent);
            }
          });
        }
        break;
    }
  }

  @Override
  public int getItemCount() {
    return busSchedules.getBusNameList().size();
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
}
