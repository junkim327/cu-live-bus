package com.example.junyoung.uiucbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.endpoints.StopTimesEndPoints;
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
  private String busTripId;
  private String busShapeId;
  private String busVehicleId;
  private ArrayList<StopTimes> stopTimesList;

  private BusRoutesAdapter adapter;
  private LayoutManager layoutManager;

  @BindView(R.id.toolbar_bus_routes)
  Toolbar busRoutesToolbar;
  @BindView(R.id.appbar_layout_bus_routes)
  AppBarLayout busRoutesAppBarLayout;
  @BindView(R.id.recycler_view_bus_routes)
  RecyclerView busRoutesRecyclerView;
  @BindView(R.id.textview_bus_name_bus_routes)
  TextView busNameTextView;
  @BindView(R.id.collapsing_toolbar_layout_bus_routes)
  CollapsingToolbarLayout collapsingToolbarLayout;
  @BindView(R.id.imageButton)
  ImageButton imageButton;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bus_routes);
    ButterKnife.bind(this);

    Intent intent = getIntent();

    busName = intent.getStringExtra(BusDeparturesActivity.EXTRA_BUSNAME);
    busTripId = intent.getStringExtra(BusDeparturesActivity.EXTRA_TRIPID);
    busShapeId = intent.getStringExtra(BusDeparturesActivity.EXTRA_SHAPEID);
    busVehicleId = intent.getStringExtra(BusDeparturesActivity.EXTRA_VEHICLEID);

    setSupportActionBar(busRoutesToolbar);

    busNameTextView.setText(busName);

    collapsingToolbarLayout.setTitleEnabled(false);
    busRoutesAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      boolean isShow = true;
      int scrollRange = -1;

      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
          busRoutesToolbar.setTitle(busName);
          busRoutesToolbar.setTitleTextColor(getResources().getColor(R.color.white));
          isShow = true;
        } else if (isShow) {
          busRoutesToolbar.setTitle(" ");
          isShow = false;
        }
      }
    });

    stopTimesList = new ArrayList<>();

    busRoutesRecyclerView.setHasFixedSize(true);

    layoutManager = new LinearLayoutManager(this);
    busRoutesRecyclerView.setLayoutManager(layoutManager);

    adapter = new BusRoutesAdapter(this, stopTimesList);
    busRoutesRecyclerView.setAdapter(adapter);

    StopTimesEndPoints service =
      RetrofitBuilder.getRetrofitInstance().create(StopTimesEndPoints.class);
    Call<StopTimesByTrip> call = service.getStopTimesByTrip(
      Constants.API_KEY,
      busTripId
    );

    call.enqueue(new Callback<StopTimesByTrip>() {
      @Override
      public void onResponse(Call<StopTimesByTrip> call, Response<StopTimesByTrip> response) {
        stopTimesList = response.body().getStopTimes();
        if (stopTimesList != null) {
          adapter.updateStopTimesList(stopTimesList);
        }
      }

      @Override
      public void onFailure(Call<StopTimesByTrip> call, Throwable t) {

      }
    });

    imageButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.imageButton:
        Intent intent = new Intent(this, BusPathOnMapActivity.class);
        intent.putExtra(EXTRA_SHAPEID, busShapeId);
        intent.putExtra(BusDeparturesActivity.EXTRA_BUSNAME, busName);
        intent.putExtra(BusDeparturesActivity.EXTRA_VEHICLEID, busVehicleId);

        startActivity(intent);
    }
  }
}
