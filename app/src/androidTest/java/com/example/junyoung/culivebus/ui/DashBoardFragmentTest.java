package com.example.junyoung.culivebus.ui;

import com.example.junyoung.culivebus.room.entity.UserSavedBusStop;
import com.example.junyoung.culivebus.testing.SingleFragmentActivity;
import com.example.junyoung.culivebus.ui.dashboard.DashboardFragment;
import com.example.junyoung.culivebus.httpclient.pojos.FavoriteBusDepartures;
import com.example.junyoung.culivebus.ui.viewmodel.BusDepartureViewModel;
import com.example.junyoung.culivebus.vo.Resource;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
public class DashBoardFragmentTest {
  @Rule
  public ActivityTestRule<SingleFragmentActivity> activityRule =
    new ActivityTestRule<>(SingleFragmentActivity.class, true, true);

  private DashboardFragment mDashboardFragment;
  private BusDepartureViewModel mDepartureViewModel;
  private MutableLiveData<Resource<List<FavoriteBusDepartures>>> response = new MutableLiveData<>();
  private List<UserSavedBusStop> mUserSavedBusStopList = new ArrayList<>();

  @Before
  public void init() {
    mDashboardFragment = new DashboardFragment();
    mDepartureViewModel = mock(BusDepartureViewModel.class);
    when(mDepartureViewModel.getResponse()).thenReturn(response);
    doNothing().when(mDepartureViewModel).loadUserFavoriteDepartures(anyList());

    activityRule.getActivity().setFragment(mDashboardFragment);
  }

  @Test
  public void testViewVisibility_whenNoSavedBusStop() {

    //when(mDataSource.loadAllBusStopByUid(anyString())).thenReturn(Flowable.empty());
    //mSavedBusStopViewModel.loadAllBusStopsByUid(anyString()).test().assertNoValues();
    //onView(withId(R.id.image_when_no_favorite_bus_stops_dashboard)).check(matches(isDisplayed()));
  }

  @Test
  public void nullDeparturesWhenNoSavedBusStops() {
    response.postValue(null);
    //onView(withId(R.id.image_when_no_favorite_bus_stops_dashboard)).check(matches(isDisplayed()));
  }

  @Test
  public void testPassingSuccessResponse() {
    response.postValue(Resource.success(null));
  }

}
