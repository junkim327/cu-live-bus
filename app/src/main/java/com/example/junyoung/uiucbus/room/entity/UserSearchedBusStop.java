package com.example.junyoung.uiucbus.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(tableName = "user_searched_bus_stop_table",
  primaryKeys = {"uid", "stop_id"})
public class UserSearchedBusStop {
  @NonNull
  @ColumnInfo(name = "uid")
  private String mUid;
  @NonNull
  @ColumnInfo(name = "stop_id")
  private String mStopId;
  @ColumnInfo(name = "stop_code")
  private String mStopCode;
  @ColumnInfo(name = "stop_name")
  private String mStopName;
  @ColumnInfo(name = "latitude")
  private double mLatitiude;
  @ColumnInfo(name = "longitude")
  private double mLongitude;

  public UserSearchedBusStop(@NonNull String uid, @NonNull String stopId, String stopCode,
                             String stopName, double latitude, double longitude) {
    mUid = uid;
    mStopId = stopId;
    mStopCode = stopCode;
    mStopName = stopName;
    mLatitiude = latitude;
    mLongitude = longitude;
  }

  @NonNull
  public String getUid() {
    return mUid;
  }

  @NonNull
  public String getStopId() {
    return mStopId;
  }

  public String getStopCode() {
    return mStopCode;
  }

  public String getStopName() {
    return mStopName;
  }

  public double getLatitiude() {
    return mLatitiude;
  }

  public double getLongitude() {
    return mLongitude;
  }

  public void setUid(@NonNull String uid) {
    mUid = uid;
  }

  public void setStopId(@NonNull String stopId) {
    mStopId = stopId;
  }

  public void setStopCode(String stopCode) {
    mStopCode = stopCode;
  }

  public void setStopName(String stopName) {
    mStopName = stopName;
  }

  public void setLatitiude(double latitiude) {
    mLatitiude = latitiude;
  }

  public void setLongitude(double longitude) {
    mLongitude = longitude;
  }
}
