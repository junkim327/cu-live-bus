package com.example.junyoung.uiucbus.httpclient.pojos;

import java.util.ArrayList;

public class NotifyItemData {
  private BusSchedules busSchedules;
  private ArrayList<Integer> notifyDataInsertedList = null;
  private ArrayList<Integer> notifyDataChangedList = null;

  public NotifyItemData(BusSchedules busSchedules,
                        ArrayList<Integer> notifyDataInsertedList,
                        ArrayList<Integer> notifyDataChangedList) {
    this.busSchedules = busSchedules;
    this.notifyDataInsertedList = notifyDataInsertedList;
    this.notifyDataChangedList = notifyDataChangedList;
  }

  public BusSchedules getBusSchedules() {
    return busSchedules;
  }

  public ArrayList<Integer> getNotifyDataInsertedList() {
    return notifyDataInsertedList;
  }

  public ArrayList<Integer> getNotifyDataChangedList() {
    return notifyDataChangedList;
  }
}
