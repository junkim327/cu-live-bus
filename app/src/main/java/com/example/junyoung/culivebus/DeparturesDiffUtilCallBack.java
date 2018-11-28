package com.example.junyoung.culivebus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.example.junyoung.culivebus.httpclient.pojos.Departure;

import java.util.ArrayList;

public class DeparturesDiffUtilCallBack extends DiffUtil.Callback {
  ArrayList<Departure> newList;
  ArrayList<Departure> oldList;

  public DeparturesDiffUtilCallBack(ArrayList<Departure> newList, ArrayList<Departure> oldList) {
    this.newList = newList;
    this.oldList = oldList;
  }

  @Override
  public int getOldListSize() {
    return oldList != null ? oldList.size() : 0;
  }

  @Override
  public int getNewListSize() {
    return newList != null ? newList.size() : 0;
  }

  @Override
  public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
    return newList.get(newItemPosition).getVehicleId()
      .equals(oldList.get(oldItemPosition).getVehicleId());
  }

  @Override
  public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
    int result = newList.get(newItemPosition).compareTo(oldList.get(oldItemPosition));

    return result == 0;
  }

  @Nullable
  @Override
  public Object getChangePayload(int oldItemPosition, int newItemPosition) {
    Departure newDeparture = newList.get(newItemPosition);
    Departure oldDeparture = oldList.get(oldItemPosition);

    Bundle diff = new Bundle();

    if (!newDeparture.getExpected().equals(oldDeparture.getExpected())) {
      diff.putString("expected", newDeparture.getExpected());
    }

    if (diff.size() == 0) {
      return null;
    }

    return diff;
  }
}
