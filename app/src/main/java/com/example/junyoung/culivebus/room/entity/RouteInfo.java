package com.example.junyoung.culivebus.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(tableName = "route_info_table",
  primaryKeys = {"uid", "starting_point_name", "destination_name"})
public class RouteInfo {
  @NonNull
  private String uid;
  @ColumnInfo(name = "origin_lat")
  private String originLat;
  @ColumnInfo(name = "origin_lon")
  private String originLon;
  @NonNull
  @ColumnInfo(name = "starting_point_name")
  private String startingPointName;
  @ColumnInfo(name = "destination_lat")
  private String destinationLat;
  @ColumnInfo(name = "destination_lon")
  private String destinationLon;
  @NonNull
  @ColumnInfo(name = "destination_name")
  private String destinationName;

  public RouteInfo() {

  }

  public RouteInfo(@NonNull String uid, String originLat, String originLon,
                   @NonNull String startingPointName, String destinationLat, String destinationLon,
                   @NonNull String destinationName) {
    this.uid = uid;
    this.originLat = originLat;
    this.originLon = originLon;
    this.startingPointName = startingPointName;
    this.destinationLat = destinationLat;
    this.destinationLon = destinationLon;
    this.destinationName = destinationName;
  }

  public RouteInfo(String originLat, String originLon, @NonNull String startingPointName,
                   String destinationLat, String destinationLon, @NonNull String destinationName) {
    this.originLat = originLat;
    this.originLon = originLon;
    this.startingPointName = startingPointName;
    this.destinationLat = destinationLat;
    this.destinationLon = destinationLon;
    this.destinationName = destinationName;
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

  public void setStartingPointName(String startingPointName) {
    this.startingPointName = startingPointName;
  }

  public void setDestinationName(String destinationName) {
    this.destinationName = destinationName;
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

  public String getStartingPointName() {
    return startingPointName;
  }

  public String getDestinationName() {
    return destinationName;
  }
}
