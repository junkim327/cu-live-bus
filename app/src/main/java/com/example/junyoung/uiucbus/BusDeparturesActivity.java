package com.example.junyoung.uiucbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

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

  @BindView(R.id.toolbar_bus_departures) Toolbar busDeparturesToolbar;
  @BindView(R.id.recycler_view_bus_departures) RecyclerView busDeparturesRecyclerView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bus_departures);
    ButterKnife.bind(this);

    setSupportActionBar(busDeparturesToolbar);

    departures = new ArrayList<>();

    busDeparturesRecyclerView.setHasFixedSize(true);

    layoutManager = new LinearLayoutManager(this);
    busDeparturesRecyclerView.setLayoutManager(layoutManager);

    adapter = new BusDeparturesAdpater(departures);
    busDeparturesRecyclerView.setAdapter(adapter);

    DeparturesEndpoints service =
      RetrofitBuilder.getRetrofitInstance().create(DeparturesEndpoints.class);
    Call<DeparturesByStop> call = service.getDeparturesByStop(
      Constants.API_KEY,
      "CAMPUSCIR:2"
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
}
