package com.example.junyoung.culivebus.httpclient.pojos;

import androidx.annotation.Nullable;

import com.example.junyoung.culivebus.room.entity.UserSavedBusStop;
import com.example.junyoung.culivebus.vo.SortedDeparture;

public class FavoriteBusDepartures {
  private boolean mIsBusDeparture;
  @Nullable
  private SortedDeparture mDeparture;
  @Nullable
  private UserSavedBusStop mBusStop;

  public FavoriteBusDepartures(boolean isBusDeparture,
                               @Nullable SortedDeparture departure,
                               @Nullable UserSavedBusStop busStop) {
    mIsBusDeparture = isBusDeparture;
    mDeparture = departure;
    mBusStop = busStop;
  }

  public boolean isBusDeparture() {
    return mIsBusDeparture;
  }

  @Nullable
  public SortedDeparture getDeparture() {
    return mDeparture;
  }

  @Nullable
  public UserSavedBusStop getBusStop() {
    return mBusStop;
  }
}
