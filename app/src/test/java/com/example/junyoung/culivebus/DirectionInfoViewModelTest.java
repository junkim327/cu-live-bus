package com.example.junyoung.culivebus;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.junyoung.culivebus.room.entity.RouteInfo;
import com.example.junyoung.culivebus.ui.viewmodel.DirectionInfoViewModel;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

public class DirectionInfoViewModelTest {
  @Rule
  public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

  @Mock
  private RouteInfoDataSource mDataSource;

  @Captor
  private ArgumentCaptor<RouteInfo> mRouteInfoCaptor;

  private DirectionInfoViewModel mViewModel;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mViewModel = new DirectionInfoViewModel(mDataSource);
  }

  @Test
  public void getAllRouteInfo_whenNoRouteInfoInserted() throws InterruptedException {
    when(mDataSource.getAllRouteInfo()).thenReturn(Flowable.<List<RouteInfo>>empty());

    mViewModel.getAllRouteInfo()
      .test()
      .assertNoValues();
  }

  @Test
  public void getAllRouteInfo_whenRouteInfoSaved() throws InterruptedException {
    RouteInfo routeInfo = new RouteInfo("Junyoung", "32.33", "33.33", "33.33", "33.33", "22.22");
    List<RouteInfo> routeInfoList = new ArrayList<>();
    routeInfoList.add(routeInfo);
    when(mDataSource.getAllRouteInfo()).thenReturn(Flowable.just(routeInfoList));

    mViewModel.getAllRouteInfo()
      .test()
      .assertValue(
        routeInfos -> {
          return routeInfos != null && routeInfos.get(0).getUid().equals(routeInfo.getUid());
        }
      );
  }
}
