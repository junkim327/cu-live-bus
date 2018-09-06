package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class VehicleData {
  @Expose
  private ArrayList<Vehicle> vehicles;

  public ArrayList<Vehicle> getVehicles() {
    return vehicles;
  }
}
