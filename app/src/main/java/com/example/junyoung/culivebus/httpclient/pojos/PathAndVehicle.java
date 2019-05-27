package com.example.junyoung.culivebus.httpclient.pojos;

import com.example.junyoung.culivebus.vo.response.VehicleResponse;

public class PathAndVehicle {
  private Path path;
  private VehicleResponse vehicleResponse;

  public PathAndVehicle(Path path, VehicleResponse vehicleResponse) {
    this.path = path;
    this.vehicleResponse = vehicleResponse;
  }

  public Path getPath() {
    return path;
  }

  public VehicleResponse getVehicleResponse() {
    return vehicleResponse;
  }
}
