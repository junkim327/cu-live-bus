package com.example.junyoung.culivebus.httpclient.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Route implements Parcelable {
  @SerializedName("route_color")
  private String routeColor;
  @SerializedName("route_long_name")
  private String routeLongName;
  @SerializedName("route_short_name")
  private String routeShortName;
  @SerializedName("route_text_color")
  private String routeTextColor;

  public Route(String routeColor, String routeLongName,
               String routeShortName, String routeTextColor) {
    this.routeColor = routeColor;
    this.routeLongName = routeLongName;
    this.routeShortName = routeShortName;
    this.routeTextColor = routeTextColor;
  }

  public String getRouteColor() {
    return routeColor;
  }

  public String getRouteLongName() {
    return routeLongName;
  }

  public String getRouteShortName() {
    return routeShortName;
  }

  public String getRouteTextColor() {
    return routeTextColor;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.routeColor);
    dest.writeString(this.routeLongName);
    dest.writeString(this.routeShortName);
    dest.writeString(this.routeTextColor);
  }

  public Route() {
  }

  protected Route(Parcel in) {
    this.routeColor = in.readString();
    this.routeLongName = in.readString();
    this.routeShortName = in.readString();
    this.routeTextColor = in.readString();
  }

  public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
    @Override
    public Route createFromParcel(Parcel source) {
      return new Route(source);
    }

    @Override
    public Route[] newArray(int size) {
      return new Route[size];
    }
  };
}
