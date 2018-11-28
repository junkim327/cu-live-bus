package com.example.junyoung.culivebus.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;

public class UtilConnection {
  public static boolean isInternetConnected(ConnectivityManager cm,
                                         OnInternetConnectedListener callback,
                                         boolean shouldHideFragment) {
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    callback.onInternetConnected(isConnected, shouldHideFragment);

    return isConnected;
  }
}
