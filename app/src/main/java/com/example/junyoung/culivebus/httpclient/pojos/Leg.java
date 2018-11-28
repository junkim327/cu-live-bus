package com.example.junyoung.culivebus.httpclient.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Leg implements Parcelable {
  @Expose
  private String type;
  @Expose
  private Walk walk;
  @Expose
  private ArrayList<Service> services = null;

  public String getType() {
    return type;
  }

  public Walk getWalk() {
    return walk;
  }

  public ArrayList<Service> getServices() {
    return services;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.type);
    dest.writeParcelable(this.walk, flags);
    dest.writeList(this.services);
  }

  public Leg() {
  }

  protected Leg(Parcel in) {
    this.type = in.readString();
    this.walk = in.readParcelable(Walk.class.getClassLoader());
    this.services = new ArrayList<Service>();
    in.readList(this.services, Service.class.getClassLoader());
  }

  public static final Parcelable.Creator<Leg> CREATOR = new Parcelable.Creator<Leg>() {
    @Override
    public Leg createFromParcel(Parcel source) {
      return new Leg(source);
    }

    @Override
    public Leg[] newArray(int size) {
      return new Leg[size];
    }
  };
}
