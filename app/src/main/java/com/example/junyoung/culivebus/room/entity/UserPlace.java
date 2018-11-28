package com.example.junyoung.culivebus.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(tableName = "place_table", primaryKeys = {"uid", "latitude", "longitude"})
public class UserPlace {
  @NonNull
  @ColumnInfo(name = "uid")
  private String mUid;
  @NonNull
  @ColumnInfo(name = "latitude")
  private double mLatitude;
  @NonNull
  @ColumnInfo(name = "longitude")
  private double mLongitude;
  @ColumnInfo(name = "place_name")
  private String mPlaceName;

  public UserPlace(@NonNull String uid, @NonNull double latitude, @NonNull double longitude,
                   String placeName) {
    mUid = uid;
    mLatitude = latitude;
    mLongitude = longitude;
    mPlaceName = placeName;
  }

  @NonNull
  public String getUid() {
    return mUid;
  }

  @NonNull
  public double getLatitude() {
    return mLatitude;
  }

  @NonNull
  public double getLongitude() {
    return mLongitude;
  }

  public String getPlaceName() {
    return mPlaceName;
  }

  public void setUid(@NonNull String mUid) {
    this.mUid = mUid;
  }

  public void setLatitude(@NonNull double mLatitude) {
    this.mLatitude = mLatitude;
  }

  public void setLongitude(@NonNull double mLongitude) {
    this.mLongitude = mLongitude;
  }

  public void setPlaceName(String placeName) {
    mPlaceName = placeName;
  }
}
