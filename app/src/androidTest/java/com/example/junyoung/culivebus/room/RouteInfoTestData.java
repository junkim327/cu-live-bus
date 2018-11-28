package com.example.junyoung.culivebus.room;

import com.example.junyoung.culivebus.room.entity.RouteInfo;

import java.util.Arrays;
import java.util.List;

public class RouteInfoTestData {
  static final RouteInfo ROUTE_INFO1 = new RouteInfo("junyoung", "32.123456", "42.123456",
    "Campus Circle", "33.123456", "43.123456", "Illini Union");
  static final RouteInfo ROUTE_INFO2 = new RouteInfo("jason", "32.234567", "42.234567",
    "Spurlock museum", "33.234567", "43.234567", "County Market");
  static final List<RouteInfo> ROUTE_INFO_LIST = Arrays.asList(ROUTE_INFO1, ROUTE_INFO2);
}
