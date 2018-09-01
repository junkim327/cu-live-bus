package com.example.junyoung.uiucbus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.DiffUtil.DiffResult;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.endpoints.DeparturesEndpoints;
import com.example.junyoung.uiucbus.httpclient.pojos.Departure;
import com.example.junyoung.uiucbus.httpclient.pojos.DeparturesByStop;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusDeparturesActivity extends AppCompatActivity {
  public static final String EXTRA_TRIPID = "com.example.junyoung.uiucbus.EXTRA_TRIPID";
  public static final String EXTRA_BUSNAME = "com.example.junyoung.uiucbus.EXTRA_BUSNAME";
  public static final String EXTRA_SHAPEID = "com.example.junyoung.uiucbus.EXTRA_SHAPEID";
  public static final String EXTRA_BUSCOLOR = "com.example.junyoung.uiucbus.EXTRA_BUSCOLOR";
  public static final String EXTRA_VEHICLEID = "com.example.junyoung.uiucbus.EXTRA_VEHICLED";

  private String busStopId;
  private String busStopCode;
  private String busStopName;
  private ArrayList<Departure> departures;

  private BusDeparturesAdpater adapter;
  private RecyclerView.LayoutManager layoutManager;
  private DeparturesEndpoints service;
  private Call<DeparturesByStop> call;

  @BindView(R.id.toolbar_bus_departures)
  Toolbar busDeparturesToolbar;
  @BindView(R.id.progressbar_bus_departures)
  ProgressBar progressBar;
  @BindView(R.id.appbar_layout_bus_departures)
  AppBarLayout appBarLayout;
  @BindView(R.id.recycler_view_bus_departures)
  RecyclerView busDeparturesRecyclerView;
  @BindView(R.id.textview_bus_stop_name_bus_departures)
  TextView busStopNameTextView;
  @BindView(R.id.textview_bus_stop_code_bus_departures)
  TextView busStopCodeTextView;
  @BindView(R.id.floating_action_button_bus_departures)
  FloatingActionButton floatingActionButton;
  @BindView(R.id.collapsing_toolbar_layout_bus_departures)
  CollapsingToolbarLayout collapsingToolbarLayout;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bus_departures);
    ButterKnife.bind(this);

    Intent intent = getIntent();

    String activityName = intent.getStringExtra(MainActivity.EXTRA_ACTIVITYNAME);
    if (activityName.equals(BusStopSearchActivity.TAG)) {
      busStopId = intent.getStringExtra(BusStopSearchActivity.EXTRA_STOPID);
      busStopCode = intent.getStringExtra(BusStopSearchActivity.EXTRA_CODE);
      busStopName = intent.getStringExtra(BusStopSearchActivity.EXTRA_STOPNAME);
    } else if (activityName.equals(BusStopsInMapActivity.TAG)) {
      busStopId = intent.getStringExtra(BusStopsInMapActivity.EXTRA_STOPID);
      busStopCode = intent.getStringExtra(BusStopsInMapActivity.EXTRA_CODE);
      busStopName = intent.getStringExtra(BusStopsInMapActivity.EXTRA_STOPNAME);
    }

    setSupportActionBar(busDeparturesToolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    busStopNameTextView.setText(busStopName);
    busStopCodeTextView.setText(busStopCode);

    collapsingToolbarLayout.setTitleEnabled(true);

    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      boolean isShow = true;
      int scrollRange = -1;

      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
          collapsingToolbarLayout.setTitle(busStopCode);
          collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
          isShow = true;
        } else if (isShow) {
          collapsingToolbarLayout.setTitle(" ");
          isShow = false;
        }
      }
    });

    departures = new ArrayList<>();

    busDeparturesRecyclerView.setHasFixedSize(true);

    layoutManager = new LinearLayoutManager(this);
    busDeparturesRecyclerView.setLayoutManager(layoutManager);

    adapter = new BusDeparturesAdpater(this, departures, busStopName);
    busDeparturesRecyclerView.setAdapter(adapter);

    service = RetrofitBuilder.getRetrofitInstance().create(DeparturesEndpoints.class);
    call = service.getDeparturesByStop(Constants.API_KEY, busStopId);

    call.enqueue(new Callback<DeparturesByStop>() {
      @Override
      public void onResponse(@NonNull Call<DeparturesByStop> call, @NonNull Response<DeparturesByStop> response) {
        ArrayList<Departure> departures = response.body().getDepartures();
        if (departures != null) {
          adapter.updateDepartureList(departures);
          progressBar.setVisibility(View.GONE);
        }
        // TODO: implement else statement.
      }

      @Override
      public void onFailure(Call<DeparturesByStop> call, Throwable t) {
        // TODO: Display toast.
      }
    });

    floatingActionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        rotateFabForward();
        call.clone().enqueue(new Callback<DeparturesByStop>() {
          @Override
          public void onResponse(Call<DeparturesByStop> call, Response<DeparturesByStop> response) {
            if (response.isSuccessful()) {
              DeparturesByStop departuresByStop = response.body();
              if (departuresByStop != null) {
                departures = departuresByStop.getDepartures();
                adapter.updateDepartureList(departures);
              }
            }
          }

          @Override
          public void onFailure(Call<DeparturesByStop> call, Throwable t) {

          }
        });
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
      case R.id.action_home:
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.home_menu, menu);

    return super.onCreateOptionsMenu(menu);
  }

  private void updateBusArrivalTime() {
    Handler timeHandler = new Handler();
  }

  private void rotateFabForward() {
    Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
    floatingActionButton.startAnimation(animRotate);
  }
}
