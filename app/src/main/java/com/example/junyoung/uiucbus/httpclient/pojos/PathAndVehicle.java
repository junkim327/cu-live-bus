package com.example.junyoung.uiucbus.httpclient.pojos;

public class PathAndVehicle {
  private Path path;
  private VehicleData vehicleData;

  public PathAndVehicle(Path path, VehicleData vehicleData) {
    this.path = path;
    this.vehicleData = vehicleData;
  }

  public Path getPath() {
    return path;
  }

  public VehicleData getVehicleData() {
    return vehicleData;
  }
}
