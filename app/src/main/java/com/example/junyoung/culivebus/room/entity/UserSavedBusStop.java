package com.example.junyoung.culivebus.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.annotation.NonNull;

@Entity(tableName = "user_saved_bus_stop_table",
  primaryKeys = {"uid", "saved_stop_id"})
public class UserSavedBusStop {
  @NonNull
  @ColumnInfo(name = "uid")
  private String mUid;
  @NonNull
  @ColumnInfo(name = "saved_stop_id")
  private String mSavedStopId;
  @ColumnInfo(name = "saved_stop_code")
  private String mSavedStopCode;
  @ColumnInfo(name = "saved_stop_name")
  private String mSavedStopName;

  public UserSavedBusStop(@NonNull String uid, @NonNull String savedStopId,
                          String savedStopCode, String savedStopName) {
    mUid = uid;
    mSavedStopId = savedStopId;
    mSavedStopCode = savedStopCode;
    mSavedStopName = savedStopName;
  }

  @NonNull
  public String getUid() {
    return mUid;
  }

  @NonNull
  public String getSavedStopId() {
    return mSavedStopId;
  }

  public String getSavedStopCode() {
    return mSavedStopCode;
  }

  public String getSavedStopName() {
    return mSavedStopName;
  }

  public void setUid(@NonNull String uid) {
    mUid = uid;
  }

  public void setSavedStopId(@NonNull String savedStopId) {
    mSavedStopId = savedStopId;
  }

  public void setSavedStopCode(String savedStopCode) {
    mSavedStopCode = savedStopCode;
  }

  public void setSavedStopName(String savedStopName) {
    mSavedStopName = savedStopName;
  }
}
