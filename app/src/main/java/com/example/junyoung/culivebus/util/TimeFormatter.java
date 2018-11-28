package com.example.junyoung.culivebus.util;

import android.content.Context;

import com.example.junyoung.culivebus.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeFormatter {
  public static String getTimeInterval(Context context, String startTime,
                                       String endTime, String format) {
    String result = context.getResources().getString(R.string.no_info);
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
      dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
      Date startTimeDate = dateFormat.parse(startTime);
      Date endTimeDate = dateFormat.parse(endTime);

      if (format.contentEquals("24hr")) {
        SimpleDateFormat newFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        newFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
        String newStartTime = newFormat.format(startTimeDate).toLowerCase();
        String newEndTime = newFormat.format(endTimeDate).toLowerCase();

        result = newStartTime + " - " + newEndTime;
      } else if (format.contentEquals("min")) {
        long timeInterval = endTimeDate.getTime() - startTimeDate.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInterval);
        result = String.valueOf(minutes) + "min";
      } else if (format.contentEquals("plain")) {
        long timeInterval = endTimeDate.getTime() - startTimeDate.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInterval);
        result = String.valueOf(minutes);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return result;
  }

  public static String getTimeInDifferentForamt(Context context, String time, String format) {
    String result = context.getResources().getString(R.string.no_info);
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
      dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
      Date timeDate = dateFormat.parse(time);

      if (format.contentEquals("24hr")) {
        SimpleDateFormat newFormat = new SimpleDateFormat("hh:mm", Locale.US);
        newFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
        return newFormat.format(timeDate).toLowerCase();
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return result;
  }

  public static long getRemainingArrivalTime(String cardBusEstimatedArrivalTime) {
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
