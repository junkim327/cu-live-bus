package com.example.junyoung.culivebus.ui.direction.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.binding.FragmentDataBindingComponent;
import com.example.junyoung.culivebus.databinding.SearchHistoryFragmentBinding;
import com.example.junyoung.culivebus.di.Injectable;
import com.example.junyoung.culivebus.ui.common.DirectionNavigationController;
import com.example.junyoung.culivebus.ui.direction.SharedPlaceViewModel;
import com.example.junyoung.culivebus.util.AutoClearedValue;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public class SearchHistoryFragment extends Fragment implements Injectable {
  @Inject
  ViewModelProvider.Factory viewModelFactory;
  @Inject
  DirectionNavigationController navigationController;

  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<SearchHistoryFragmentBinding> binding;
  AutoClearedValue<SearchedPlaceListAdapter> adapter;

  private SharedPlaceViewModel sharedPlaceViewModel;
  private SearchHistoryViewModel searchHistoryViewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    SearchHistoryFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.fragment_search_history, container, false, dataBindingComponent);
    binding = new AutoClearedValue<>(this, dataBinding);

    return dataBinding.getRoot();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    sharedPlaceViewModel = ViewModelProviders.of(getActivity(), viewModelFactory)
      .get(SharedPlaceViewModel.class);
    searchHistoryViewModel = ViewModelProviders.of(getActivity(), viewModelFactory)
      .get(SearchHistoryViewModel.class);

    initToolbar();
    initRecyclerView();

    SearchedPlaceListAdapter rvAdapter = new SearchedPlaceListAdapter(dataBindingComponent,
      searchedPlace -> {
        sharedPlaceViewModel.setPlace(searchedPlace);
        navigationController.navigateToResult(true);
      });
    binding.get().searchedPlaceList.setAdapter(rvAdapter);
    adapter = new AutoClearedValue<>(this, rvAdapter);
  }

  private void initToolbar() {
    Toolbar toolbar = binding.get().toolbar;
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);

    toolbar.setNavigationOnClickListener(view -> {
      FragmentManager fm = getFragmentManager();
      if (fm != null && fm.getBackStackEntryCount() > 0) {
        fm.popBackStack();
      }
    });
  }

  private void initRecyclerView() {
    searchHistoryViewModel.getSearchedPlaces().observe(this, listResource -> {
      adapter.get().replace(listResource == null ? null : listResource.data);
    });
  }
}
