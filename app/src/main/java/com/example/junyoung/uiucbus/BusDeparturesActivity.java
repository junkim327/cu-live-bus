package com.example.junyoung.uiucbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.endpoints.DeparturesEndpoints;
import com.example.junyoung.uiucbus.httpclient.pojos.Departures;
import com.example.junyoung.uiucbus.httpclient.pojos.DeparturesByStop;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BusDeparturesActivity extends AppCompatActivity {
  private ArrayList<Departures> departures;

  private BusDeparturesAdpater adapter;
  private RecyclerView.LayoutManager layoutManager;

  @BindView(R.id.toolbar_bus_departures)
  Toolbar busDeparturesToolbar;
  @BindView(R.id.appbar_layout_bus_departures)
  AppBarLayout appBarLayout;
  @BindView(R.id.recycler_view_bus_departures)
  RecyclerView busDeparturesRecyclerView;
  @BindView(R.id.textview_bus_stop_name_bus_departures)
  TextView textViewBusStopName;
  @BindView(R.id.textview_bus_stop_code_bus_departures)
  TextView textViewBusStopCode;
  @BindView(R.id.collapsing_toolbar_layout_bus_departures)
  CollapsingToolbarLayout collapsingToolbarLayout;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bus_departures);
    ButterKnife.bind(this);

    Intent intent = getIntent();

    String busStopId = intent.getStringExtra(BusStopsInMapActivity.EXTRA_STOPID);
    final String busStopCode = intent.getStringExtra(BusStopsInMapActivity.EXTRA_CODE);
    String busStopName = intent.getStringExtra(BusStopsInMapActivity.EXTRA_STOPNAME);

    setSupportActionBar(busDeparturesToolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    textViewBusStopName.setText(busStopName);
    textViewBusStopCode.setText(busStopCode);

    collapsingToolbarLayout.setTitleEnabled(false);
    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      boolean isShow = true;
      int scrollRange = -1;

      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
          busDeparturesToolbar.setTitle(busStopCode);
          isShow = true;
        } else if (isShow) {
          busDeparturesToolbar.setTitle(" ");
          isShow = false;
        }
      }
    });

    departures = new ArrayList<>();

    busDeparturesRecyclerView.setHasFixedSize(true);

    layoutManager = new LinearLayoutManager(this);
    busDeparturesRecyclerView.setLayoutManager(layoutManager);

    adapter = new BusDeparturesAdpater(this, departures);
    busDeparturesRecyclerView.setAdapter(adapter);

    DeparturesEndpoints service =
      RetrofitBuilder.getRetrofitInstance().create(DeparturesEndpoints.class);
    Call<DeparturesByStop> call = service.getDeparturesByStop(
      Constants.API_KEY,
      busStopId
    );

    call.enqueue(new Callback<DeparturesByStop>() {
      @Override
      public void onResponse(Call<DeparturesByStop> call, Response<DeparturesByStop> response) {
        ArrayList<Departures> departures = response.body().getDepartures();
        if (departures != null) {
          adapter.updateDepartureList(departures);
        }
        // TODO: implement else statement.
      }

      @Override
      public void onFailure(Call<DeparturesByStop> call, Throwable t) {
        // TODO: Display toast.
          }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
