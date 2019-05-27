package com.example.junyoung.culivebus.db.entity;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "shapeTable", primaryKeys = {"shapeId", "shapePoint"})
public class Shape {
  @NonNull
  private String shapeId;
  @NonNull
  @SerializedName("shape_pt_sequence")
  private int shapePoint;
  @SerializedName("shape_pt_lat")
  private double shapeLat;
  @SerializedName("shape_pt_lon")
  private double shapeLon;
  @SerializedName("stop_id")
  private String stopId;

  public Shape(String shapeId, int shapePoint, double shapeLat, double shapeLon, String stopId) {
    this.shapeId = shapeId;
    this.shapePoint = shapePoint;
    this.shapeLat = shapeLat;
    this.shapeLon =shapeLon;
    this.stopId = stopId;
  }

  public String getShapeId() {
    return shapeId;
  }

  public void setShapeId(String shapeId) {
    this.shapeId = shapeId;
  }

  public int getShapePoint() {
    return shapePoint;
  }

  public void setShapePoint(int shapePoint) {
    this.shapePoint = shapePoint;
  }

  public double getShapeLat() {
    return shapeLat;
  }

  public void setShapeLat(double shapeLat) {
    this.shapeLat = shapeLat;
  }

  public double getShapeLon() {
    return shapeLon;
  }

  public void setShapeLon(double shapeLon) {
    this.shapeLon = shapeLon;
  }

  public String getStopId() {
    return stopId;
  }

  public void setStopId(String stopId) {
    this.stopId = stopId;
  }
}

