package com.example.junyoung.uiucbus.httpclient.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Point implements Parcelable {
  @Expose
  private double lat;
  @Expose
  private double lon;
  @Expose
  private String name;
  @Expose
  private String time;
  @SerializedName("stop_id")
  @Expose
  private String stopId;

  public double getLat() {
    return lat;
  }

  public double getLon() {
    return lon;
  }

  public String getName() {
    return name;
  }

  public String getTime() {
    return time;
  }

  public String getStopId() {
    return stopId;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeDouble(this.lat);
    dest.writeDouble(this.lon);
    dest.writeString(this.name);
    dest.writeString(this.time);
    dest.writeString(this.stopId);
  }

  public Point() {
  }

  protected Point(Parcel in) {
    this.lat = in.readDouble();
    this.lon = in.readDouble();
    this.name = in.readString();
    this.time = in.readString();
    this.stopId = in.readString();
  }

  public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() {
    @Override
    public Point createFromParcel(Parcel source) {
      return new Point(source);
    }

    @Override
    public Point[] newArray(int size) {
      return new Point[size];
    }
  };
}
