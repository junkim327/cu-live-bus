package com.example.junyoung.culivebus.ui.direction.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.databinding.SearchedPlaceCardBinding;
import com.example.junyoung.culivebus.db.entity.SearchedPlace;
import com.example.junyoung.culivebus.ui.common.DataBoundListAdapter;
import com.example.junyoung.culivebus.util.Objects;

import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;

public class SearchedPlaceListAdapter extends
  DataBoundListAdapter<SearchedPlace, SearchedPlaceCardBinding> {
  private final DataBindingComponent dataBindingComponent;
  private final PlaceClickCallback placeClickCallback;

  public SearchedPlaceListAdapter(DataBindingComponent dataBindingComponent,
                                  PlaceClickCallback placeClickCallback) {
    this.dataBindingComponent = dataBindingComponent;
    this.placeClickCallback = placeClickCallback;
  }

  @Override
  protected SearchedPlaceCardBinding createBinding(ViewGroup parent) {
    SearchedPlaceCardBinding binding = DataBindingUtil
      .inflate(LayoutInflater.from(parent.getContext()), R.layout.card_searched_place, parent,
        false, dataBindingComponent);
    binding.getRoot().setOnClickListener(v -> {
      SearchedPlace searchedPlace = binding.getSearchedPlace();
      if (searchedPlace != null && placeClickCallback != null) {
        placeClickCallback.onClick(searchedPlace);
      }
    });

    return binding;
  }

  @Override
  protected void bind(SearchedPlaceCardBinding binding, SearchedPlace item) {
    binding.setSearchedPlace(item);
  }

  @Override
  protected boolean areItemsTheSame(SearchedPlace oldItem, SearchedPlace newItem) {
    return Objects.equals(oldItem.getId(), newItem.getId());
  }

  @Override
  protected boolean areContentsTheSame(SearchedPlace oldItem, SearchedPlace newItem) {
    return (Objects.equals(oldItem.getLatitude(), newItem.getLatitude())
      && Objects.equals(oldItem.getLongitude(), newItem.getLongitude()));
  }

  public interface PlaceClickCallback {
    void onClick(SearchedPlace searchedPlace);
  }
}
