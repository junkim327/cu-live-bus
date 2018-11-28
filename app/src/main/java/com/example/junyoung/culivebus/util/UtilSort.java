package com.example.junyoung.culivebus.util;

import com.example.junyoung.culivebus.adapter.BusFavoriteDeparturesAdapter;
import com.example.junyoung.culivebus.httpclient.pojos.BusInfo;
import com.example.junyoung.culivebus.httpclient.pojos.BusSchedules;
import com.example.junyoung.culivebus.httpclient.pojos.Departure;
import com.example.junyoung.culivebus.httpclient.pojos.NotifyItemData;

import java.util.ArrayList;

public class UtilSort {
  public static BusSchedules sortDeparturesByBus(BusSchedules busSchedules,
                                                 ArrayList<Departure> departures,
                                                 BusFavoriteDeparturesAdapter adapter) {
      for (Departure departure : departures) {
      String headSign = departure.getHeadsign();
      String expected = departure.getExpected();
      String vehicleId = departure.getVehicleId();
      String tripId = departure.getTrip().getTripId();
      String shapeId = departure.getTrip().getShapeId();

      if (!busSchedules.getBusNameList().contains(headSign)) {
        String routeColor = departure.getRoute().getRouteColor();
        String tripHeadSign = departure.getTrip().getTripHeadSign();
        String routeTextColor = departure.getRoute().getRouteTextColor();

        busSchedules.addBusName(headSign);
        busSchedules.addBusInfo(
          new BusInfo(
            headSign, tripHeadSign, tripId, shapeId, vehicleId, expected, routeColor, routeTextColor
          )
        );
      } else {
        int busInfoIndex = busSchedules.getBusNameList().indexOf(headSign);
        busSchedules.getBusInfoList().get(busInfoIndex).setBusInfo(
          tripId, shapeId, expected, vehicleId
        );
      }
    }

    return busSchedules;
  }

  public static BusSchedules sortDeparturesByBus2(String busStopName,
                                                  BusSchedules busSchedules,
                                                  ArrayList<Departure> departures,
                                                  ArrayList<Integer> bannerIndex) {
    int startPosition = busSchedules.getBusNameList().size() - 1;
    bannerIndex.add(startPosition);
    for (Departure departure : departures) {
      String headSign = departure.getHeadsign() + "," + busStopName;
      String expected = departure.getExpected();
      String vehicleId = departure.getVehicleId();
      String tripId = departure.getTrip().getTripId();
      String shapeId = departure.getTrip().getShapeId();

      int endPosition = busSchedules.getBusNameList().size();
      if (!busSchedules.getBusNameList().subList(startPosition, endPosition).contains(headSign)) {
        String routeColor = departure.getRoute().getRouteColor();
        String tripHeadSign = departure.getTrip().getTripHeadSign();
        String routeTextColor = departure.getRoute().getRouteTextColor();

        busSchedules.addBusName(headSign);
        busSchedules.addBusInfo(
          new BusInfo(
            headSign, tripHeadSign, tripId, shapeId, vehicleId, expected, routeColor, routeTextColor
          )
        );
      } else {
        int busInfoIndex = busSchedules.getBusNameList().subList(startPosition, endPosition)
          .indexOf(headSign) + startPosition;
        busSchedules.getBusInfoList().get(busInfoIndex).setBusInfo(
          tripId, shapeId, expected, vehicleId
        );
      }
    }

    return busSchedules;
  }

  public static NotifyItemData sortDeparturesByBus3(BusSchedules busSchedules,
                                                    ArrayList<Departure> departures,
                                                    int startPosition) {
    ArrayList<Integer> itemInsertedIndex = new ArrayList<>();
    ArrayList<Integer> itemChangedIndex = new ArrayList<>();
    for (Departure departure : departures) {
      String headSign = departure.getHeadsign() + "," + busSchedules.getBusNameList()
        .get(startPosition);
      String expected = departure.getExpected();
      String vehicleId = departure.getVehicleId();
      String tripId = departure.getTrip().getTripId();
      String shapeId = departure.getTrip().getShapeId();

      int endPosition = busSchedules.getBusNameList().size();
      if (!busSchedules.getBusNameList().subList(startPosition, endPosition).contains(headSign)) {
        String routeColor = departure.getRoute().getRouteColor();
        String tripHeadSign = departure.getTrip().getTripHeadSign();
        String routeTextColor = departure.getRoute().getRouteTextColor();

        //
        busSchedules.addBusName(headSign);
        busSchedules.addBusInfo(
          new BusInfo(
            headSign, tripHeadSign, tripId, shapeId, vehicleId, expected, routeColor, routeTextColor
          )
        );
        itemInsertedIndex.add(endPosition);
      } else {
        int busInfoIndex = busSchedules.getBusNameList().subList(startPosition, endPosition)
          .indexOf(headSign) + startPosition;
        BusInfo busInfo = busSchedules.getBusInfoList().get(busInfoIndex);
        if (busInfo.getVehicleId().contains(vehicleId)) {
          if (!busInfo.getExpected().get(busInfo.getVehicleId().indexOf(vehicleId)).contentEquals(
            expected
          )) {
            busInfo.setExpectedAtPosition(expected, busInfo.getVehicleId().indexOf(vehicleId));
            itemChangedIndex.add(busInfoIndex);
          }
        } else {
          busInfo.setBusInfo(tripId, shapeId, expected, vehicleId);
          itemChangedIndex.add(busInfoIndex);
        }
      }
    }

    return new NotifyItemData(busSchedules, itemInsertedIndex, itemChangedIndex);
  }
}
