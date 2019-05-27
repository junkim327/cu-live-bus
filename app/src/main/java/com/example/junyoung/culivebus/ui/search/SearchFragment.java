package com.example.junyoung.culivebus.ui.search;

import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.binding.FragmentDataBindingComponent;
import com.example.junyoung.culivebus.databinding.SearchFragmentBinding;
import com.example.junyoung.culivebus.ui.MainNavigationFragment;
import com.example.junyoung.culivebus.ui.common.MainNavigationController;
import com.example.junyoung.culivebus.ui.common.SharedStopPointViewModel;
import com.example.junyoung.culivebus.ui.departure.DepartureActivity;
import com.example.junyoung.culivebus.util.AutoClearedValue;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.util.UtilConnection;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;

public class SearchFragment extends DaggerFragment implements MainNavigationFragment {
  @Inject
  ViewModelProvider.Factory viewModelFactory;

  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<SearchFragmentBinding> binding;
  AutoClearedValue<BusStopListAdapter> adapter;

  private SearchViewModel searchViewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    SearchFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.fragment_search, container, false, dataBindingComponent);
    binding = new AutoClearedValue<>(this, dataBinding);

    return dataBinding.getRoot();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    searchViewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);

    initRecyclerView();

    BusStopListAdapter rvAdapter = new BusStopListAdapter(dataBindingComponent, stopPoint -> {
      if (stopPoint != null) {
        stopPoint.setRecentSearched(true);
        searchViewModel.updateBusStop(stopPoint);
        Timber.d(stopPoint.getStopId());
        startActivity(DepartureActivity.starterIntent(getContext(), stopPoint));
      }
    });
    binding.get().busStopList.setAdapter(rvAdapter);
    adapter = new AutoClearedValue<>(this, rvAdapter);

    initSearchView();

    binding.get().setCallback(() -> {
      FragmentManager fm = getFragmentManager();
      if (fm != null && fm.getBackStackEntryCount() > 0) {
        fm.popBackStack();
      }
    });
  }

  private void initRecyclerView() {
    searchViewModel.setQuery("");
    searchViewModel.getBusStops().observe(this, busStopResource -> {
      binding.get().setBusStopResource(busStopResource);
      adapter.get().replace(busStopResource == null ? null : busStopResource.data);
      binding.get().executePendingBindings();
    });

  }

  private void initSearchView() {
    binding.get().searchView.setIconified(false);
    //binding.get().searchView.setIconifiedByDefault(false);
    binding.get().searchView.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override
      public boolean onClose() {
        return true;
      }
    });

    initSearchQueryTextListener();
  }

  private void initSearchQueryTextListener() {
    binding.get().searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        searchViewModel.setQuery(newText);
        Timber.d(newText);
        if (newText.contentEquals("")) {
          Timber.d("Empty String");
        }
        return false;
      }
    });
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.options_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      FragmentManager fm = getFragmentManager();
      if (fm != null && fm.getBackStackEntryCount() > 0) {
        fm.popBackStack();
      }
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
