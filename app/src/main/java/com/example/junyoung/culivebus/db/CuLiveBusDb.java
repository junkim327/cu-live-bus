package com.example.junyoung.culivebus.db;

import com.example.junyoung.culivebus.db.dao.BusStopDao;
import com.example.junyoung.culivebus.db.dao.ChangesetDao;
import com.example.junyoung.culivebus.db.dao.FavoriteStopDao;
import com.example.junyoung.culivebus.db.dao.RouteInfoDao;
import com.example.junyoung.culivebus.db.dao.SearchedPlaceDao;
import com.example.junyoung.culivebus.db.dao.ShapeDao;
import com.example.junyoung.culivebus.db.entity.Changeset;
import com.example.junyoung.culivebus.db.entity.FavoriteStop;
import com.example.junyoung.culivebus.db.entity.RouteInfo;
import com.example.junyoung.culivebus.db.entity.SearchedPlace;
import com.example.junyoung.culivebus.db.entity.Shape;
import com.example.junyoung.culivebus.db.entity.StopPointFts;
import com.example.junyoung.culivebus.db.entity.StopPoint;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StopPoint.class, StopPointFts.class, FavoriteStop.class, Shape.class,
  Changeset.class, SearchedPlace.class, RouteInfo.class}, version = 1)
public abstract class CuLiveBusDb extends RoomDatabase {
  abstract public BusStopDao busStopDao();

  abstract public FavoriteStopDao favoriteStopDao();

  abstract public ChangesetDao changesetDao();

  abstract public ShapeDao shapeDao();

  abstract public SearchedPlaceDao searchedPlaceDao();

  abstract public RouteInfoDao routeInfoDao();
}
