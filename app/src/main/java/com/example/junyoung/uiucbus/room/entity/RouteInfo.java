package com.example.junyoung.uiucbus.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "route_info_table")
public class RouteInfo {
  @PrimaryKey(autoGenerate = true)
  private int key;
  @NonNull
  private String uid;
  @ColumnInfo(name = "origin_lat")
  private String originLat;
  @ColumnInfo(name = "origin_lon")
  private String originLon;
  @ColumnInfo(name = "destination_lat")
  private String destinationLat;
  @ColumnInfo(name = "destination_lon")
  private String destinationLon;

  public RouteInfo() {

  }

  public RouteInfo(@NonNull String uid, String originLat, String originLon,
                   String destinationLat, String destinationLon) {
    this.uid = uid;
    this.originLat = originLat;
    this.originLon = originLon;
    this.destinationLat = destinationLat;
    this.destinationLon = destinationLon;
  }

  public void setKey(int key) {
    this.key = key;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public void setOriginLat(String originLat) {
    this.originLat = originLat;
  }

  public void setOriginLon(String originLon) {
    this.originLon = originLon;
  }

  public void setDestinationLat(String destinationLat) {
    this.destinationLat = destinationLat;
  }

  public void setDestinationLon(String destinationLon) {
    this.destinationLon = destinationLon;
  }

  public int getKey() {
    return key;
  }

  @NonNull
  public String getUid() {
    return uid;
  }

  public String getOriginLat() {
    return originLat;
  }

  public String getOriginLon() {
    return originLon;
  }

  public String getDestinationLat() {
    return destinationLat;
  }

  public String getDestinationLon() {
    return destinationLon;
  }
}
