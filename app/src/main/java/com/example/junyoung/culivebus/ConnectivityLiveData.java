package com.example.junyoung.culivebus;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.lifecycle.LiveData;

public class ConnectivityLiveData extends LiveData<Boolean> {
  private ConnectivityManager connectivityManager;
  private ConnectivityManager.NetworkCallback networkCallback;

  public ConnectivityLiveData(ConnectivityManager connectivityManager) {
    this.connectivityManager = connectivityManager;
    this.networkCallback = new ConnectivityManager.NetworkCallback() {
      @Override
      public void onAvailable(Network network) {
        postValue(true);
      }

      @Override
      public void onLost(Network network) {
        postValue(false);
      }
    };
  }

  @Override
  protected void onActive() {
    super.onActive();

    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
    if (activeNetwork != null && activeNetwork.isConnected()) {
      postValue(true);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      connectivityManager.registerDefaultNetworkCallback(networkCallback);
    } else {
      connectivityManager.registerNetworkCallback(
        new NetworkRequest.Builder().build(),
        networkCallback
      );
    }
  }

  @Override
  protected void onInactive() {
    super.onInactive();
    connectivityManager.unregisterNetworkCallback(networkCallback);
  }
}
