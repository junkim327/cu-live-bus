package com.example.junyoung.culivebus.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.annotation.NonNull;
import androidx.room.Ignore;

@Entity(tableName = "routeInfoTable", primaryKeys = {"startingPointId", "destinationId"})
public class RouteInfo {
  @NonNull
  private String startingPointId;
  private double startingPointLat;
  private double startingPointLon;
  private String startingPointName;
  @NonNull
  private String destinationId;
  private double destinationLat;
  private double destinationLon;
  private String destinationName;

  @Ignore
  public RouteInfo() {

  }

  public RouteInfo(@NonNull String startingPointId, double startingPointLat,
                   double startingPointLon, String startingPointName,
                   @NonNull String destinationId, double destinationLat,
                   double destinationLon, String destinationName) {
    this.startingPointId = startingPointId;
    this.startingPointLat = startingPointLat;
    this.startingPointLon = startingPointLon;
    this.startingPointName = startingPointName;
    this.destinationId = destinationId;
    this.destinationLat = destinationLat;
    this.destinationLon = destinationLon;
    this.destinationName = destinationName;
  }

  @Ignore
  public RouteInfo(double startingPointLat, double startingPointLon, @NonNull String startingPointName,
                   double destinationLat, double destinationLon, @NonNull String destinationName) {
    this.startingPointLat = startingPointLat;
    this.startingPointLon = startingPointLon;
    this.startingPointName = startingPointName;
    this.destinationLat = destinationLat;
    this.destinationLon = destinationLon;
    this.destinationName = destinationName;
  }

  public void setStartingPoint(String startingPointId, double startingPointLat,
                               double startingPointLon, String startingPointName) {
    this.startingPointId = startingPointId;
    this.startingPointLat = startingPointLat;
    this.startingPointLon = startingPointLon;
    this.startingPointName = startingPointName;
  }

  public void setStartingPointId(String startingPointId) {
    this.startingPointId = startingPointId;
  }

  public void setStartingPointLat(double startingPointLat) {
    this.startingPointLat = startingPointLat;
  }

  public void setStartingPointLon(double startingPointLon) {
    this.startingPointLon = startingPointLon;
  }

  public void setStartingPointName(String startingPointName) {
    this.startingPointName = startingPointName;
  }

  public void setDestination(String destinationId, double destinationLat,
                             double destinationLon, String destinationName) {
    this.destinationId = destinationId;
    this.destinationLat = destinationLat;
    this.destinationLon = destinationLon;
    this.destinationName = destinationName;
  }

  public void setDestinationId(String destinationId) {
    this.destinationId = destinationId;
  }

  public void setDestinationLat(double destinationLat) {
    this.destinationLat = destinationLat;
  }

  public void setDestinationLon(double destinationLon) {
    this.destinationLon = destinationLon;
  }

  public void setDestinationName(String destinationName) {
    this.destinationName = destinationName;
  }

  @NonNull
  public String getStartingPointId() {
    return startingPointId;
  }

  public double getStartingPointLat() {
    return startingPointLat;
  }

  public double getStartingPointLon() {
    return startingPointLon;
  }

  public String getStartingPointName() {
    return startingPointName;
  }

  @NonNull
  public String getDestinationId() {
    return destinationId;
  }

  public double getDestinationLat() {
    return destinationLat;
  }

  public double getDestinationLon() {
    return destinationLon;
  }

  public String getDestinationName() {
    return destinationName;
  }
}
