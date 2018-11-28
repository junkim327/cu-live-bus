package com.example.junyoung.culivebus.httpclient.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Path implements Parcelable {
  @Expose
  private ArrayList<Shape> shapes;

  public ArrayList<Shape> getShapes() {
    return shapes;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeList(this.shapes);
  }

  public Path() {
  }

  protected Path(Parcel in) {
    this.shapes = new ArrayList<Shape>();
    in.readList(this.shapes, Shape.class.getClassLoader());
  }

  public static final Parcelable.Creator<Path> CREATOR = new Parcelable.Creator<Path>() {
    @Override
    public Path createFromParcel(Parcel source) {
      return new Path(source);
    }

    @Override
    public Path[] newArray(int size) {
      return new Path[size];
    }
  };
}
