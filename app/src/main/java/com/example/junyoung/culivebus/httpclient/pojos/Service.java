package com.example.junyoung.culivebus.httpclient.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Service implements Parcelable {
  @Expose
  private Point end;
  @Expose
  private Trip trip;
  @Expose
  private Route route;
  @Expose
  private Point begin;

  public Point getEnd() {
    return end;
  }

  public Trip getTrip() {
    return trip;
  }

  public Route getRoute() {
    return route;
  }

  public Point getBegin() {
    return begin;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.end, flags);
    dest.writeParcelable(this.trip, flags);
    dest.writeParcelable(this.route, flags);
    dest.writeParcelable(this.begin, flags);
  }

  public Service() {
  }

  protected Service(Parcel in) {
    this.end = in.readParcelable(Point.class.getClassLoader());
    this.trip = in.readParcelable(Trip.class.getClassLoader());
    this.route = in.readParcelable(Route.class.getClassLoader());
    this.begin = in.readParcelable(Point.class.getClassLoader());
  }

  public static final Parcelable.Creator<Service> CREATOR = new Parcelable.Creator<Service>() {
    @Override
    public Service createFromParcel(Parcel source) {
      return new Service(source);
    }

    @Override
    public Service[] newArray(int size) {
      return new Service[size];
    }
  };
}
