package com.example.junyoung.culivebus.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Itinerary implements Parcelable {
  @SerializedName("start_time")
  private String startTime;
  @SerializedName("end_time")
  private String endTime;
  @SerializedName("travel_time")
  private String travelTime;
  @Expose
  private ArrayList<Leg> legs;

  public String getStartTime() {
    return startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public String getTravelTime() {
    return travelTime;
  }

  public ArrayList<Leg> getLegs() {
    return legs;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.startTime);
    dest.writeString(this.endTime);
    dest.writeString(this.travelTime);
    dest.writeList(this.legs);
  }

  public Itinerary() {
  }

  protected Itinerary(Parcel in) {
    this.startTime = in.readString();
    this.endTime = in.readString();
    this.travelTime = in.readString();
    this.legs = new ArrayList<Leg>();
    in.readList(this.legs, Leg.class.getClassLoader());
  }

  public static final Parcelable.Creator<Itinerary> CREATOR = new Parcelable.Creator<Itinerary>() {
    @Override
    public Itinerary createFromParcel(Parcel source) {
      return new Itinerary(source);
    }

    @Override
    public Itinerary[] newArray(int size) {
      return new Itinerary[size];
    }
  };
}
