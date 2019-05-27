package com.example.junyoung.culivebus.ui.direction.result;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.databinding.DirectionResultCardBinding;
import com.example.junyoung.culivebus.vo.Itinerary;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class DirectionResultListAdapter
  extends RecyclerView.Adapter<DirectionResultListAdapter.DirectionResultListViewHolder> {
  @Nullable
  private List<Itinerary> itineraries;
  private final DataBindingComponent dataBindingComponent;
  private final DirectionResultCallback directionResultCallback;

  public static class DirectionResultListViewHolder extends RecyclerView.ViewHolder {
    public final DirectionResultCardBinding binding;

    DirectionResultListViewHolder(DirectionResultCardBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }
  }

  public DirectionResultListAdapter(DataBindingComponent dataBindingComponent,
                                    DirectionResultCallback directionResultCallback) {
    this.dataBindingComponent = dataBindingComponent;
    this.directionResultCallback = directionResultCallback;
  }

  public void updateItineraries(List<Itinerary> itineraries) {
    this.itineraries = itineraries;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public DirectionResultListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    DirectionResultCardBinding binding = DataBindingUtil
      .inflate(LayoutInflater.from(parent.getContext()), R.layout.card_direction_result, parent,
        false, dataBindingComponent);
    binding.getRoot().setOnClickListener(v -> {
      Itinerary itinerary = binding.getItinerary();
      if (itinerary != null && directionResultCallback != null) {
        directionResultCallback.onClick(itinerary);
      }
    });

    return new DirectionResultListViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull DirectionResultListViewHolder holder, int position) {
    ItineraryAdapter adapter = new ItineraryAdapter(
      dataBindingComponent,
      itineraries.get(position).getLegs()
    );
    holder.binding.legList.setAdapter(adapter);
    holder.binding.setItinerary(itineraries.get(position));
    holder.binding.executePendingBindings();
  }

  @Override
  public int getItemCount() {
    return itineraries == null ? 0 : itineraries.size();
  }

  public interface DirectionResultCallback {
    void onClick(Itinerary itinerary);
  }
}
