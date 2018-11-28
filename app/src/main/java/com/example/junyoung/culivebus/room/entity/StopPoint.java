package com.example.junyoung.culivebus.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "user_searched_bus_stop_table",
  primaryKeys = {"uid", "stop_id"})
public class StopPoint {
  @NonNull
  @ColumnInfo(name = "uid")
  private String mUid;
  @NonNull
  @ColumnInfo(name = "stop_id")
  @SerializedName("stop_id")
  private String mStopId;
  @ColumnInfo(name = "stop_code")
  @SerializedName("code")
  private String mStopCode;
  @ColumnInfo(name = "stop_name")
  @SerializedName("stop_name")
  private String mStopName;
  @ColumnInfo(name = "latitude")
  @SerializedName("stop_lat")
  private double mLatitude;
  @ColumnInfo(name = "longitude")
  @SerializedName("stop_lon")
  private double mLongitude;

  @Ignore
  private Double mDistance;

  public StopPoint(@NonNull String uid, @NonNull String stopId, String stopCode,
                   String stopName, double latitude, double longitude) {
    mUid = uid;
    mStopId = stopId;
    mStopCode = stopCode;
    mStopName = stopName;
    mLatitude = latitude;
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

  public double getLatitude() {
    return mLatitude;
  }

  public double getLongitude() {
    return mLongitude;
  }

  public Double getDistance() {
    return mDistance;
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

  public void setLatitude(double latitude) {
    mLatitude = latitude;
  }

  public void setLongitude(double longitude) {
    mLongitude = longitude;
  }

  public void setDistance(Double distance) {
    mDistance = distance;
  }
}
