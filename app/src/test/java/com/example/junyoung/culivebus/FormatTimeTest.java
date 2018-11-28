package com.example.junyoung.culivebus;

import org.junit.Test;

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
}
