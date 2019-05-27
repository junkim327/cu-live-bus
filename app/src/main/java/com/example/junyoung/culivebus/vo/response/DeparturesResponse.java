package com.example.junyoung.culivebus.vo.response;

import com.example.junyoung.culivebus.httpclient.pojos.Departure;

import java.util.List;

public class DeparturesResponse {
  private List<Departure> departures;

  public List<Departure> getDepartures() {
    return departures;
  }
}
