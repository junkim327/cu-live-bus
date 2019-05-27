package com.example.junyoung.culivebus.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favoriteStopTable")
public class FavoriteStop {
  @NonNull
  @PrimaryKey
  private String stopId;
  private String stopCode;
  private String stopName;

  public FavoriteStop(@NonNull String stopId, String stopCode, String stopName) {
    this.stopId = stopId;
    this.stopCode = stopCode;
    this.stopName = stopName;
  }

  public String getStopId() {
    return stopId;
  }

  public void setStopId(String stopId) {
    this.stopId = stopId;
  }

  public String getStopCode() {
    return stopCode;
  }

  public void setStopCode(String stopCode) {
    this.stopCode = stopCode;
  }

  public String getStopName() {
    return stopName;
  }

  public void setStopName(String stopName) {
    this.stopName = stopName;
  }
}
