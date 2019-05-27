package com.example.junyoung.culivebus.ui.route;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.databinding.BottomSheetBusInfoBinding;
import com.example.junyoung.culivebus.databinding.BottomSheetDragCardBinding;
import com.example.junyoung.culivebus.databinding.BottomSheetRouteCardBinding;
import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.example.junyoung.culivebus.vo.StopTime;
import com.example.junyoung.culivebus.ui.common.SharedDepartureViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.RecyclerView.Adapter;

public class RouteListAdapter extends Adapter<ViewHolder> {
  @Nullable
  private List<StopTime> stopTimes;
  private LifecycleOwner lifecycleOwner;
  private RouteViewModel routeViewModel;

  public RouteListAdapter(RouteViewModel routeViewModel,
                          LifecycleOwner lifecycleOwner) {
    this.lifecycleOwner = lifecycleOwner;
    this.routeViewModel = routeViewModel;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ViewHolder viewHolder;

    switch (viewType) {
      case R.layout.card_bottom_sheet_drag:
        viewHolder = new DragViewHolder(
          BottomSheetDragCardBinding.inflate(
            LayoutInflater.from(parent.getContext()),
            parent,
            false
          )
        );
        break;
      case R.layout.card_bottom_sheet_bus_info:
        viewHolder = new BusInfoViewHolder(
          BottomSheetBusInfoBinding.inflate(
            LayoutInflater.from(parent.getContext()),
            parent,
            false
          ), lifecycleOwner,
          routeViewModel
        );
        break;
      default:
        BottomSheetRouteCardBinding binding =
          BottomSheetRouteCardBinding.inflate(
            LayoutInflater.from(parent.getContext()),
            parent,
            false
          );
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

          }
        });
        viewHolder = new RouteViewHolder(binding, lifecycleOwner);
        break;
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    if (holder.getItemViewType() == R.layout.card_bottom_sheet_bus_info) {
      ((BusInfoViewHolder) holder).bind();
    } else if (holder.getItemViewType() == R.layout.card_bottom_sheet_route) {
      ((RouteViewHolder) holder).bind(stopTimes.get(position - 2).getStopPoint());
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return R.layout.card_bottom_sheet_drag;
    } else if (position == 1) {
      return R.layout.card_bottom_sheet_bus_info;
    } else {
      return R.layout.card_bottom_sheet_route;
    }
  }

  @Override
  public int getItemCount() {
    return stopTimes == null ? 0 : (stopTimes.size() + 2);
  }

  public void updateStopTimes(List<StopTime> stopTimes) {
    this.stopTimes = stopTimes;
    notifyDataSetChanged();
  }

  public static class DragViewHolder extends ViewHolder {
    DragViewHolder(BottomSheetDragCardBinding binding) {
      super(binding.getRoot());
    }
  }

  public static class BusInfoViewHolder extends ViewHolder {
    private final BottomSheetBusInfoBinding binding;
    private final LifecycleOwner lifecycleOwner;
    private final RouteViewModel routeViewModel;

    BusInfoViewHolder(BottomSheetBusInfoBinding binding, LifecycleOwner lifecycleOwner,
                      RouteViewModel routeViewModel) {
      super(binding.getRoot());
      this.binding = binding;
      this.lifecycleOwner = lifecycleOwner;
      this.routeViewModel = routeViewModel;
    }

    public void bind() {
      binding.setViewModel(routeViewModel);
      binding.setLifecycleOwner(lifecycleOwner);
    }
  }

  public static class RouteViewHolder extends ViewHolder {
    private final BottomSheetRouteCardBinding binding;
    private final LifecycleOwner lifecycleOwner;

    RouteViewHolder(BottomSheetRouteCardBinding binding, LifecycleOwner lifecycleOwner) {
      super(binding.getRoot());
      this.binding = binding;
      this.lifecycleOwner = lifecycleOwner;
    }

    public void bind(StopPoint stopPoint) {
      binding.setStopPoint(stopPoint);
      binding.setLifecycleOwner(lifecycleOwner);
    }
  }
}
