package com.example.junyoung.culivebus.ui.departure;

import android.widget.TextView;

import com.example.junyoung.culivebus.R;

import org.threeten.bp.Duration;

import androidx.databinding.BindingAdapter;

public class DepartureListBindingAdapter {
  @BindingAdapter("duration")
  public static void setTimeUntilDeparture(TextView textView, Duration duration) {
    if (duration != null) {
      long totalSeconds = duration.getSeconds();
      if (totalSeconds <= 0) {
        textView.setText(textView.getContext().getString(R.string.arrived));
      } else if (totalSeconds < 60) {
        textView.setText(textView.getContext().getString(R.string.soon));
      } else {
        long minutes = duration.toMinutes();
        long seconds = totalSeconds - (minutes * 60);
        textView.setText(textView.getContext().getString(
          R.string.time_until_departure_minutes_and_seconds, minutes, seconds));
      }
    }
  }
}
