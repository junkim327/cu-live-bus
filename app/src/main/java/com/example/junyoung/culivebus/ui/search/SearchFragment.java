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
import androidx.fragment.app.Fragment;
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
import com.example.junyoung.culivebus.di.Injectable;
import com.example.junyoung.culivebus.ui.common.BusStopListAdapter;
import com.example.junyoung.culivebus.ui.common.NavigationController;
import com.example.junyoung.culivebus.ui.viewmodel.BusStopViewModel;
import com.example.junyoung.culivebus.util.AutoClearedValue;
import com.example.junyoung.culivebus.util.listener.OnInternetConnectedListener;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.ui.viewmodel.SharedStopPointViewModel;
import com.example.junyoung.culivebus.util.UtilConnection;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class SearchFragment extends Fragment implements Injectable {
  @Inject
  ViewModelProvider.Factory viewModelFactory;
  @Inject
  NavigationController navigationController;

  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<SearchFragmentBinding> binding;
  AutoClearedValue<BusStopListAdapter> adapter;

  private String mUid;
  private int mClickTag;
  private boolean mIsInternetConnected = true;
  private ConnectivityManager mConnectivityManager;
  private OnInternetConnectedListener mInternetConnectedCallback;

  // ViewModels
  private SearchViewModel searchViewModel;
  private SharedStopPointViewModel mSharedStopPointViewModel;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    try {
      mInternetConnectedCallback = (OnInternetConnectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnInternetConnectedListener.");
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (mConnectivityManager != null) {
      mIsInternetConnected = UtilConnection.isInternetConnected(mConnectivityManager,
        mInternetConnectedCallback, false);
    }

    SearchFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.search_fragment, container, false, dataBindingComponent);
    binding = new AutoClearedValue<>(this, dataBinding);
    return dataBinding.getRoot();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    searchViewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);

    initRecyclerView();

    BusStopListAdapter rvAdapter = new BusStopListAdapter(dataBindingComponent, stopPoint -> {
      stopPoint.setRecentSearched(true);
      searchViewModel.updateBusStop(stopPoint);
      navigationController.navigateToDeparture(stopPoint);
    });
    binding.get().busStopList.setAdapter(rvAdapter);
    adapter = new AutoClearedValue<>(this, rvAdapter);

    mSharedStopPointViewModel = ViewModelProviders.of(getActivity())
      .get(SharedStopPointViewModel.class);

    initSearchView();
  }

  private void initRecyclerView() {
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

    initSearchQueryTestListener();
  }

  private void initSearchQueryTestListener() {
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

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    mUid = sharedPref.getString(getString(R.string.saved_uid), null);
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
