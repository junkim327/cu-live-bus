package com.example.junyoung.culivebus;

import android.os.CountDownTimer;

import org.junit.Test;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.ZonedDateTime;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatTimeTest {
  @Test
  public void convert_24hr_format_to_12hr_format_isCorrect() throws Exception {
    String time = "22:34";

    try {
      SimpleDateFormat format = new SimpleDateFormat("H:mm");
      Date date = format.parse(time);
      System.out.println(date);
      String newFormatTime = new SimpleDateFormat("hh:mm a").format(date);
      System.out.println(newFormatTime);
      assertTrue(newFormatTime.contentEquals("10:34 PM"));
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void to_lower_case_isCorrect() throws Exception {
    String time = "10:34 PM";
    String lowerCaseTime = time.toLowerCase();
    System.out.println(lowerCaseTime);
    assertTrue(lowerCaseTime.contentEquals("10:34 pm"));
  }

  @Test
  public void duration_test() throws Exception {
    ZonedDateTime startTime = ZonedDateTime.parse("2019-01-18T21:45:00-06:00");
    System.out.println(Instant.now());
    Duration timeUntilDeparture = Duration.between(Instant.now(), startTime);
    System.out.println(Duration.between(Instant.now(), startTime).toMinutes());
    System.out.println(Duration.between(Instant.now(), startTime).getSeconds());


  }

  @Test
  public void plus_seconds_test_isCorrect() {
    Duration twoSeconds = Duration.ofSeconds(2);
    Duration fourSeconds = twoSeconds.plusSeconds(2);
    assertEquals(fourSeconds.getSeconds(), 4);
  }
}
