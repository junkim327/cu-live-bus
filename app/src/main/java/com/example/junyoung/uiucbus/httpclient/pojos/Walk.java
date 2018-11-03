package com.example.junyoung.uiucbus.httpclient.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Walk implements Parcelable {
  @Expose
  private Point end;
  @Expose
  private Point begin;
  @Expose
  private double distance;

  public Point getEnd() {
    return end;
  }

  public Point getBegin() {
    return begin;
  }

  public double getDistance() {
    return distance;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.end, flags);
    dest.writeParcelable(this.begin, flags);
    dest.writeDouble(this.distance);
  }

  public Walk() {
  }

  protected Walk(Parcel in) {
    this.end = in.readParcelable(Point.class.getClassLoader());
    this.begin = in.readParcelable(Point.class.getClassLoader());
    this.distance = in.readDouble();
  }

  public static final Parcelable.Creator<Walk> CREATOR = new Parcelable.Creator<Walk>() {
    @Override
    public Walk createFromParcel(Parcel source) {
      return new Walk(source);
    }

    @Override
    public Walk[] newArray(int size) {
      return new Walk[size];
    }
  };
}
