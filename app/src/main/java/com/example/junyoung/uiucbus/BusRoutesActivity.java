package com.example.junyoung.uiucbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.junyoung.uiucbus.adapters.BusRoutesAdapter;
import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.services.StopTimesServices;
import com.example.junyoung.uiucbus.httpclient.pojos.StopTimes;
import com.example.junyoung.uiucbus.httpclient.pojos.StopTimesByTrip;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusRoutesActivity extends AppCompatActivity implements View.OnClickListener {
  public static final String EXTRA_SHAPEID = "com.example.junyoung.uiucbus.EXTRA_SHAPEID";

  private String busName;
  private String busColor;
  private String busStopName;
  private String parentActivity;
  private ArrayList<String> busTripIdList;
  private ArrayList<String> busShapeIdList;
  private ArrayList<String> busVehicleIdList;
  private ArrayList<StopTimes> stopTimesList = null;

  private BusRoutesAdapter adapter;
  private LayoutManager layoutManager;

  @BindView(R.id.toolbar_bus_routes)
  Toolbar busRoutesToolbar;
  @BindView(R.id.appbar_layout_bus_routes)
  AppBarLayout busRoutesAppBarLayout;
  @BindView(R.id.collapsing_toolbar_layout_bus_routes)
  CollapsingToolbarLayout collapsingToolbarLayout;

  @BindView(R.id.textview_bus_routes)
  TextView busRouteTextView;
  @BindView(R.id.textview_bus_name_bus_routes)
  TextView busNameTextView;

  @BindView(R.id.recycler_view_bus_routes)
  RecyclerView busRoutesRecyclerView;
  @BindView(R.id.view_map_button_bus_routes)
  ImageButton viewMapButton;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bus_routes);
    ButterKnife.bind(this);

    Intent intent = getIntent();

    busName = intent.getStringExtra(BusDeparturesActivity.EXTRA_BUSNAME);
    busColor = intent.getStringExtra(BusDeparturesActivity.EXTRA_BUSCOLOR);
    busStopName = intent.getStringExtra(BusStopsInMapActivity.EXTRA_STOPNAME);
    busTripIdList = intent.getStringArrayListExtra(BusDeparturesActivity.EXTRA_TRIPID);
    busShapeIdList = intent.getStringArrayListExtra(BusDeparturesActivity.EXTRA_SHAPEID);
    busVehicleIdList = intent.getStringArrayListExtra(BusDeparturesActivity.EXTRA_VEHICLEID);

    parentActivity = intent.getStringExtra(MainActivity.EXTRA_ACTIVITYNAME);

    setSupportActionBar(busRoutesToolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    busNameTextView.setText(busName);
    busRouteTextView.setText(busVehicleIdList.get(0));

    collapsingToolbarLayout.setTitleEnabled(true);

    busRoutesAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      boolean isShow = true;
      int scrollRange = -1;

      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
          viewMapButton.setVisibility(View.INVISIBLE);
          busNameTextView.setVisibility(View.INVISIBLE);
          busRouteTextView.setVisibility(View.INVISIBLE);
          collapsingToolbarLayout.setTitle(busName);
          collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
          isShow = true;
        } else if (isShow) {
          viewMapButton.setVisibility(View.VISIBLE);
          busNameTextView.setVisibility(View.VISIBLE);
          busRouteTextView.setVisibility(View.VISIBLE);
          collapsingToolbarLayout.setTitle(" ");
          isShow = false;
        }
      }
    });

    setRecyclerView();

    StopTimesServices service =
      RetrofitBuilder.getRetrofitInstance().create(StopTimesServices.class);
    Call<StopTimesByTrip> call = service.getStopTimesByTrip(
      Constants.API_KEY,
      busTripIdList.get(0)
    );

    call.enqueue(new Callback<StopTimesByTrip>() {
      @Override
      public void onResponse(@NonNull Call<StopTimesByTrip> call, @NonNull Response<StopTimesByTrip> response) {
        if (response.isSuccessful()) {
          stopTimesList = response.body().getStopTimes();
          adapter.updateStopTimesList(stopTimesList);
          for (int i = 0; i < stopTimesList.size(); i++) {
            if (stopTimesList.get(i).getStopPoint().getStopName().equals(busStopName)) {
              if ((stopTimesList.size() - i) <= 7) {
                busRoutesRecyclerView.scrollToPosition(stopTimesList.size() - 1);
              }
              busRoutesRecyclerView.scrollToPosition(i);
            }
          }
          busRoutesAppBarLayout.setExpanded(false);
          Log.d("BusRoutesActivity", "First");
        }
      }

      @Override
      public void onFailure(Call<StopTimesByTrip> call, Throwable t) {

      }
    });

    Log.d("BusRoutesActivity", "Second");
    viewMapButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.view_map_button_bus_routes:
        Intent intent = new Intent(this, BusPathOnMapActivity.class);
        intent.putExtra(BusDeparturesActivity.EXTRA_BUSNAME, busName);
        intent.putStringArrayListExtra(EXTRA_SHAPEID, busShapeIdList);
        intent.putStringArrayListExtra(BusDeparturesActivity.EXTRA_VEHICLEID, busVehicleIdList);

        startActivity(intent);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.home_menu, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        if (parentActivity.contentEquals(MainActivity.TAG)) {
          Intent intent = new Intent(this, MainActivity.class);
          NavUtils.navigateUpTo(this, intent);
        }
        return true;
      case R.id.action_home:
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    return super.onOptionsItemSelected(item);
  }

  public void setRecyclerView() {
    stopTimesList = new ArrayList<>();
    layoutManager = new LinearLayoutManager(this);
    adapter = new BusRoutesAdapter(this, stopTimesList);

    busRoutesRecyclerView.setAdapter(adapter);
    busRoutesRecyclerView.setHasFixedSize(true);
    busRoutesRecyclerView.setLayoutManager(layoutManager);
    busRoutesRecyclerView.addItemDecoration(new RouteItemDecoration(getApplicationContext(), busColor));
  }
}
