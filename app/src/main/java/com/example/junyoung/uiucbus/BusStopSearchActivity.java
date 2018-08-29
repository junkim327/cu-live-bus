package com.example.junyoung.uiucbus;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junyoung.uiucbus.httpclient.RetrofitBuilder;
import com.example.junyoung.uiucbus.httpclient.endpoints.BusStopsEndpoints;
import com.example.junyoung.uiucbus.httpclient.pojos.BusStops;
import com.example.junyoung.uiucbus.httpclient.pojos.Stop;
import com.example.junyoung.uiucbus.httpclient.pojos.StopPoint;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusStopSearchActivity extends AppCompatActivity {
  public static final String TAG = "BusStopSearchActivity";
  public static final String EXTRA_CODE = "com.example.junyoung.uiucbus.EXTRA_CODE";
  public static final String EXTRA_STOPID = "com.example.junyoung.uiucbus.EXTRA_STOPID";
  public static final String EXTRA_STOPNAME = "com.example.junyoung.uiucbus.EXTRA_STOPNAME";

  private ArrayList<StopPoint> stopPointList = new ArrayList<>();

  private SearchView searchView;
  private BusStopsAdapter adapter;
  private LayoutManager layoutManager;

  @BindView(R.id.toolbar_bus_stop_search)
  Toolbar toolbar;
  @BindView(R.id.recyclerview_bus_stop_search)
  RecyclerView recyclerView;
  @BindView(R.id.imageview_warning_sign_bus_stop_search)
  ImageView warningImageView;
  @BindView(R.id.textview_no_results_bus_stop_search)
  TextView noResultsTextView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bus_stop_search);
    ButterKnife.bind(this);

    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayShowTitleEnabled(false);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black);
    }

    recyclerView.setHasFixedSize(true);

    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new BusStopsAdapter(this, stopPointList);
    recyclerView.setAdapter(adapter);

    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
      recyclerView.getContext(),
      DividerItemDecoration.VERTICAL
    );
    recyclerView.addItemDecoration(dividerItemDecoration);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.options_menu, menu);

    SearchManager searchManager =
      (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    searchView.setSearchableInfo(
      searchManager.getSearchableInfo(getComponentName())
    );

    searchView.setIconified(false);
    searchView.setQueryHint(getResources().getString(R.string.search_hint));
    searchView.setMaxWidth(Integer.MAX_VALUE);
    searchView.requestFocus();

    ImageView searchIcon =
      searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
    searchIcon.setImageDrawable(null);

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        if (stopPointList == null || stopPointList.isEmpty()) {
          recyclerView.setVisibility(View.INVISIBLE);
          warningImageView.setVisibility(View.VISIBLE);
          noResultsTextView.setVisibility(View.VISIBLE);
        }

        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        recyclerView.setVisibility(View.VISIBLE);
        warningImageView.setVisibility(View.INVISIBLE);
        noResultsTextView.setVisibility(View.INVISIBLE);

        BusStopsEndpoints service =
          RetrofitBuilder.getRetrofitInstance().create(BusStopsEndpoints.class);
        Call<BusStops> call = service.getStopsBySearch(Constants.API_KEY, newText);

        call.enqueue(new Callback<BusStops>() {
          @Override
          public void onResponse(Call<BusStops> call, Response<BusStops> response) {
            if (response.isSuccessful()) {
              ArrayList<Stop> stops = response.body().getStops();

              if (stops != null && !stops.isEmpty()) {
                stopPointList = response.body().getStops().get(0).getStopPoints();
              }
              adapter.updateStopsList(stopPointList);
            }
          }

          @Override
          public void onFailure(Call<BusStops> call, Throwable t) {

          }
        });

        return false;
      }
    });

    final ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
    closeButton.setVisibility(View.GONE);
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int size = stopPointList.size();
        stopPointList.clear();
        adapter.notifyItemRangeChanged(0, size);
        searchView.setQuery("", false);
        closeButton.setVisibility(View.GONE);
      }
    });

    return true;
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
