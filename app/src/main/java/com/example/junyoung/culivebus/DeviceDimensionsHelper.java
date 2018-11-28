package com.example.junyoung.culivebus;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DeviceDimensionsHelper {
  public static float convertDpToPixel(float dp, Context context){
    Resources r = context.getResources();
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
  }

  // DeviceDimensionsHelper.convertPixelsToDp(25f, context) => (25px converted to dp)
  public static float convertPixelsToDp(float px, Context context){
    Resources r = context.getResources();
    DisplayMetrics metrics = r.getDisplayMetrics();
    float dp = px / (metrics.densityDpi / 160f);
    return dp;
  }

  public static int getScreenHeight(Activity activity) {
    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    return metrics.heightPixels;
  }
}
