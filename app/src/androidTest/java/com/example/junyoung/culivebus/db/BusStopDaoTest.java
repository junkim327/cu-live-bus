package com.example.junyoung.culivebus.db;

import com.example.junyoung.culivebus.util.TestUtil;
import com.example.junyoung.culivebus.db.entity.StopPoint;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.example.junyoung.culivebus.util.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class BusStopDaoTest extends DbTest {
  @Rule
  public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

  @Test
  public void insertAndCountBusStops() throws InterruptedException {
    List<StopPoint> busStops = TestUtil.createBusStops(7, "stopId", "stopCode", "stopName", 3, 4,
      false);
    db.busStopDao().insertAll(busStops);
    int numStops = getValue(db.busStopDao().countBusStops());
    assertThat(numStops, is(7));
  }

  @Test
  public void insertAndCheckId() throws InterruptedException {
    List<StopPoint> busStops = TestUtil.createBusStops(3, "stopId", "stopCode", "stopName", 3, 4,
    false);
    db.busStopDao().insertAll(busStops);
    List<StopPoint> loadedBusStops = getValue(db.busStopDao().loadAllBusStops());
    for (int i = 0; i < 3; i++) {
      // If the table has never before contained any data, then a AUTOGENERATED ID of 1 is used.
      assertThat(loadedBusStops.get(i).getId(), is(i + 1)); // ids should be 1, 2 and 3.
    }
  }

  @Test
  public void insertAndSearchBusStops() throws InterruptedException {
    List<StopPoint> busStops = new ArrayList<>();
    busStops.add(TestUtil.createBusStop("jb12", "mtd4233", "Campus Circle", 3, 4, false));
    busStops.add(TestUtil.createBusStop("ez94", "mtd2933", "Illini Union", 3, 4, false));
    db.busStopDao().insertAll(busStops);
    List<StopPoint> loadedBusStops = getValue(db.busStopDao().searchAllBusStops("Campus Cir*"));
    assertThat(loadedBusStops, hasSize(1));
  }

  @Test
  public void insertAndLoadRecentlySearchedBusStops() throws InterruptedException {
    List<StopPoint> busStops = new ArrayList<>();
    busStops.add(TestUtil.createBusStop("jb12", "mtd4233", "Campus Circle", 3, 4, true));
    busStops.add(TestUtil.createBusStop("ez94", "mtd2933", "Illini Union", 3, 4, true));
    busStops.add(TestUtil.createBusStop("he23", "mtd3920", "Home", 3, 4, false));
    db.busStopDao().insertAll(busStops);
    List<StopPoint> recentlySearchedBusStops = getValue(
      db.busStopDao().loadRecentlySearchedBusStops()
    );
    assertThat(recentlySearchedBusStops, hasSize(2));
  }
}
