package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Path {
  @Expose
  private ArrayList<Shape> shapes;

  public ArrayList<Shape> getShapes() {
    return shapes;
  }
}
