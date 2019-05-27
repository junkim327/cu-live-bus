package com.example.junyoung.culivebus.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "placeTable")
public class SearchedPlace {
  @NonNull
  @PrimaryKey
  private String id;
  private double latitude;
  private double longitude;
  private String placeName;
  private String address;

  public SearchedPlace(@NonNull String id, double latitude, double longitude, String placeName,
                       String address) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.placeName = placeName;
    this.address = address;
  }

  @Ignore
  public SearchedPlace(@NonNull String id, double latitude, double longitude, String placeName) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.placeName = placeName;
  }

  @NonNull
  public String getId() {
    return this.id;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public String getPlaceName() {
    return placeName;
  }

  public String getAddress() {
    return address;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLatitude(double mLatitude) {
    this.latitude = mLatitude;
  }

  public void setLongitude(double mLongitude) {
    this.longitude = mLongitude;
  }

  public void setPlaceName(String placeName) {
    this.placeName = placeName;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
