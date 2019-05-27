package com.example.junyoung.culivebus.ui.direction.result;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.databinding.ItineraryCardBinding;
import com.example.junyoung.culivebus.vo.Leg;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ItineraryAdapter
  extends RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder> {
  @Nullable
  private List<Leg> legs;
  private final DataBindingComponent dataBindingComponent;

  public static class ItineraryViewHolder extends RecyclerView.ViewHolder {
    public final ItineraryCardBinding binding;

    ItineraryViewHolder(ItineraryCardBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }
  }

  public ItineraryAdapter(DataBindingComponent dataBindingComponent, @Nullable List<Leg> legs) {
    this.dataBindingComponent = dataBindingComponent;
    this.legs = new ArrayList<>();
    if (legs != null) {
      for (Leg leg : legs) {
        if (leg.getType().startsWith("S")) {
          this.legs.add(leg);
        }
      }
    }
  }

  @NonNull
  @Override
  public ItineraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItineraryCardBinding binding = DataBindingUtil
      .inflate(LayoutInflater.from(parent.getContext()), R.layout.card_itinerary, parent,
        false, dataBindingComponent);

    return new ItineraryViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ItineraryViewHolder holder, int position) {
    if (legs != null) {
      if (legs.get(position).getServices() != null && !legs.get(position).getServices().isEmpty()) {
        holder.binding.setService(legs.get(position).getServices().get(0));
        holder.binding.executePendingBindings();
      }
    }
  }

  @Override
  public int getItemCount() {
    return legs == null ? 0 : legs.size();
  }
}
