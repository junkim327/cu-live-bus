package com.example.junyoung.uiucbus.httpclient.pojos;

import android.support.annotation.Nullable;

import com.example.junyoung.uiucbus.room.entity.UserSavedBusStop;

public class FavoriteBusDepartures {
  private boolean mIsBusDeparture;
  @Nullable
  private DeparturesByStop mDeparture;
  @Nullable
  private UserSavedBusStop mBusStop;

  public FavoriteBusDepartures(boolean isBusDeparture,
                               @Nullable DeparturesByStop departure,
                               @Nullable UserSavedBusStop busStop) {
    mIsBusDeparture = isBusDeparture;
    mDeparture = departure;
    mBusStop = busStop;
  }

  public boolean isBusDeparture() {
    return mIsBusDeparture;
  }

  @Nullable
  public DeparturesByStop getDeparture() {
    return mDeparture;
  }

  @Nullable
  public UserSavedBusStop getBusStop() {
    return mBusStop;
  }
}
