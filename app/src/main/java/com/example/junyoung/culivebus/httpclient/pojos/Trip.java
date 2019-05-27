package com.example.junyoung.culivebus.httpclient.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trip implements Parcelable {
  @SerializedName("trip_id")
  private String tripId;
  @SerializedName("shape_id")
  private String shapeId;
  @Expose
  private String direction;
  @SerializedName("trip_headsign")
  private String tripHeadSign;

  public Trip(String tripId, String shapeId, String direction, String tripHeadSign) {
    this.tripId = tripId;
    this.shapeId = shapeId;
    this.direction = direction;
    this.tripHeadSign = tripHeadSign;
  }

  public String getTripId() {
    return tripId;
  }

  public String getShapeId() {
    return shapeId;
  }

  public String getDirection() {
    return direction;
  }

  public String getTripHeadSign() {
    return tripHeadSign;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.tripId);
    dest.writeString(this.shapeId);
    dest.writeString(this.direction);
    dest.writeString(this.tripHeadSign);
  }

  public Trip() {
  }

  protected Trip(Parcel in) {
    this.tripId = in.readString();
    this.shapeId = in.readString();
    this.direction = in.readString();
    this.tripHeadSign = in.readString();
  }

  public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
    @Override
    public Trip createFromParcel(Parcel source) {
      return new Trip(source);
    }

    @Override
    public Trip[] newArray(int size) {
      return new Trip[size];
    }
  };
}
