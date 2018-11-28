package com.example.junyoung.culivebus.room;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.junyoung.culivebus.RouteInfoDataSource;
import com.example.junyoung.culivebus.room.dao.RouteInfoDao;
import com.example.junyoung.culivebus.room.database.RouteInfoDatabase;
import com.example.junyoung.culivebus.room.datasource.LocalRouteInfoDataSource;
import com.example.junyoung.culivebus.room.entity.RouteInfo;
import static com.example.junyoung.culivebus.room.RouteInfoTestData.ROUTE_INFO1;
import static com.example.junyoung.culivebus.room.RouteInfoTestData.ROUTE_INFO2;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class RouteInfoDataSourceTest {

  @Rule
  public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

  private RouteInfoDao mRouteInfoDao;
  private RouteInfoDatabase mDb;
  private RouteInfoDataSource mDataSource;

  @Before
  public void createDb() {
    Context context = InstrumentationRegistry.getTargetContext();
    mDb = Room.inMemoryDatabaseBuilder(context, RouteInfoDatabase.class)
      // allowing main thread queries, just for testing
      .allowMainThreadQueries()
      .build();
    mDataSource = new LocalRouteInfoDataSource(mDb.getRouteInfoDao());
  }

  @After
  public void closeDb() throws IOException {
    mDb.close();
  }

  @Test
  public void getRouteInfoWhenNoRouteInfoInserted() throws InterruptedException {
    mDataSource.getAllRouteInfo()
      .test()
      .assertValue(
        routeInfos -> {
          return routeInfos.size() == 0;
        }
      );
  }

  @Test
  public void getRouteInfoAfterOneRouteInfoInserted() throws InterruptedException {
    mDataSource.insertRouteInfo(ROUTE_INFO1);

    mDb.getRouteInfoDao().loadAllRouteInfoByUid(ROUTE_INFO1.getUid())
      .test()
      .assertValue(
        routeInfos -> {
          return routeInfos != null && routeInfos.get(0).getUid().equals(ROUTE_INFO1.getUid());
        }
      );
  }

  @Test
  public void getAllRouteInfoAfterInserted() throws InterruptedException {
    mDataSource.insertRouteInfo(ROUTE_INFO1);
    mDataSource.insertRouteInfo(ROUTE_INFO2);
    mDataSource.getAllRouteInfo()
      .test()
      .assertValue(
        routeInfos -> {
          return routeInfos != null && routeInfos.size() == 2;
        }
      );
  }

  @Test
  public void getAllRouteInfoAfterTwoSameDirectionsInserted() throws InterruptedException {
    mDataSource.insertRouteInfo(ROUTE_INFO1);
    mDataSource.insertRouteInfo(ROUTE_INFO1);
    mDataSource.getAllRouteInfoByUid(ROUTE_INFO1.getUid())
      .test()
      .assertValue(
        routeInfos -> {
          return routeInfos != null && routeInfos.size() == 1;
        }
      );
  }

  @Test
  public void getAllRouteInfoByUid() throws InterruptedException {
    mDataSource.insertRouteInfo(ROUTE_INFO1);
    mDataSource.insertRouteInfo(ROUTE_INFO2);
    mDataSource.getAllRouteInfoByUid(ROUTE_INFO1.getUid())
      .test()
      .assertValue(
        routeInfos -> {
          return routeInfos != null && routeInfos.size() == 1;
        }
      );
  }

  @Test
  public void getAllRouteInfoAfterInsertedandDeleted() throws InterruptedException {
    mDataSource.insertRouteInfo(ROUTE_INFO1);
    mDataSource.insertRouteInfo(ROUTE_INFO2);
    mDataSource.deleteAllRouteInfoByUid(ROUTE_INFO1.getUid());
    mDataSource.getAllRouteInfoByUid(ROUTE_INFO2.getUid())
      .test()
      .assertValue(
        routeInfos -> {
          return routeInfos != null && routeInfos.get(0).getUid().equals(ROUTE_INFO2.getUid());
        }
      );
  }



  private void createRandomRouteInfo(RouteInfo routeInfo) {
    Random random = new Random();
    routeInfo.setUid(String.valueOf(random.nextInt()));
    routeInfo.setOriginLat(String.valueOf(random.nextDouble()));
    routeInfo.setOriginLon(String.valueOf(random.nextDouble()));
    routeInfo.setDestinationLat(String.valueOf(random.nextDouble()));
    routeInfo.setDestinationLon(String.valueOf(random.nextDouble()));
  }
}
