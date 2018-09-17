package com.example.junyoung.uiucbus;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeFormatter {
  public static String getTimeInterval(Context context, String startTime, String endTime) {
    String result = context.getResources().getString(R.string.no_info);
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
      dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
      Date startTimeDate = dateFormat.parse(startTime);
      Date endTimeDate = dateFormat.parse(endTime);

      SimpleDateFormat newFormat = new SimpleDateFormat("hh:mm a", Locale.US);
      newFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
      String newStartTime = newFormat.format(startTimeDate).toLowerCase();
      String newEndTime = newFormat.format(endTimeDate).toLowerCase();

      result = newStartTime + " - " + newEndTime;
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return result;
  }
}
