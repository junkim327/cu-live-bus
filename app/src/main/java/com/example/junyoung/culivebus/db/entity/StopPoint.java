package com.example.junyoung.culivebus.db.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "busStopTable")
public class StopPoint implements ClusterItem {
  @NonNull
  @PrimaryKey(autoGenerate = true)
  private int id;
  @SerializedName("stop_id")
  private String stopId;
  @SerializedName("code")
  private String stopCode;
  @SerializedName("stop_name")
  private String stopName;
  @SerializedName("stop_lat")
  private double latitude;
  @SerializedName("stop_lon")
  private double longitude;
  private boolean isRecentSearched;

  @Ignore
  public StopPoint(String stopId, String stopCode, String stopName) {
    this.stopId = stopId;
    this.stopCode = stopCode;
    this.stopName = stopName;
  }

  @Ignore
  public StopPoint(String stopId, String stopCode, String stopName,
                   double latitude, double longitude) {
    this.stopId = stopId;
    this.stopCode = stopCode;
    this.stopName = stopName;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public StopPoint(String stopId, String stopCode, String stopName,
                   double latitude, double longitude, boolean isRecentSearched) {
    this.stopId = stopId;
    this.stopCode = stopCode;
    this.stopName = stopName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.isRecentSearched = isRecentSearched;
  }

  public int getId() {
    return id;
  }

  public String getStopId() {
    return stopId;
  }

  public String getStopCode() {
    return stopCode;
  }

  public String getStopName() {
    return stopName;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  @Override
  public LatLng getPosition() {
    return new LatLng(latitude, longitude);
  }

  @Override
  public String getTitle() {
    return null;
  }

  @Override
  public String getSnippet() {
    return null;
  }

  public boolean isRecentSearched() {
    return isRecentSearched;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setStopId(@NonNull String stopId) {
    this.stopId = stopId;
  }

  public void setStopCode(String stopCode) {
    this.stopCode = stopCode;
  }

  public void setStopName(String stopName) {
    this.stopName = stopName;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public void setRecentSearched(boolean isRecentSearched) {
    this.isRecentSearched = isRecentSearched;
  }
}
