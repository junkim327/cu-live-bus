package com.example.junyoung.culivebus.db.entity;

import androidx.room.Entity;
import androidx.room.Fts4;

@Entity(tableName = "busStopFtsTable")
@Fts4(contentEntity = StopPoint.class)
public class StopPointFts {
  private String stopCode;
  private String stopName;

  public StopPointFts(String stopCode, String stopName) {
    this.stopCode = stopCode;
    this.stopName = stopName;
  }

  public String getStopCode() {
    return stopCode;
  }

  public String getStopName() {
    return stopName;
  }
}
