package com.example.junyoung.culivebus.db;

import com.example.junyoung.culivebus.db.entity.FavoriteStop;
import com.example.junyoung.culivebus.util.TestUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.example.junyoung.culivebus.util.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class FavoriteStopDaoTest extends DbTest {
  @Rule
  public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

  @Test
  public void insertAndCountFavoriteStops() throws InterruptedException {
    List<FavoriteStop> favoriteStops = TestUtil.createFavoriteStops(3, "stopId", "stopCode",
      "stopName");
    db.favoriteStopDao().insertAll(favoriteStops);
    int numStops = getValue(db.favoriteStopDao().countFavoriteStops());
    assertThat(numStops, is(3));
  }

  @Test
  public void insertAndCountStopIdList() throws InterruptedException {
    List<String> stopIdList = getValue(db.favoriteStopDao().loadStopIdList());
    assertThat(stopIdList, hasSize(0));

    List<FavoriteStop> favoriteStops = TestUtil.createFavoriteStops(3, "stopId", "stopCode",
      "stopName");
    db.favoriteStopDao().insertAll(favoriteStops);
    stopIdList = getValue(db.favoriteStopDao().loadStopIdList());
    assertThat(stopIdList, hasSize(3));
  }

  @Test
  public void insertAndDeleteAndCount() throws InterruptedException {
    List<FavoriteStop> favoriteStops = TestUtil.createFavoriteStops(3, "stopId", "stopCode",
      "stopName");
    db.favoriteStopDao().insertAll(favoriteStops);
    db.favoriteStopDao().deleteFavoriteStopByStopId("stopId1");
    List<String> stopIdList = getValue(db.favoriteStopDao().loadStopIdList());
    assertThat(stopIdList, hasSize(2));
  }
}
