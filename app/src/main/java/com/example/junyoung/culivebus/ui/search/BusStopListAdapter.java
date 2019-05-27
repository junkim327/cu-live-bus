package com.example.junyoung.culivebus.ui.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.databinding.CardBusStopBinding;
import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.example.junyoung.culivebus.ui.common.DataBoundListAdapter;
import com.example.junyoung.culivebus.util.Objects;

import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import timber.log.Timber;

public class BusStopListAdapter extends DataBoundListAdapter<StopPoint, CardBusStopBinding> {
  private final DataBindingComponent dataBindingComponent;
  private final busStopClickCallback busStopClickCallback;

  public BusStopListAdapter(DataBindingComponent dataBindingComponent,
                            busStopClickCallback busStopClickCallback) {
    this.dataBindingComponent = dataBindingComponent;
    this.busStopClickCallback = busStopClickCallback;
  }

  @Override
  protected CardBusStopBinding createBinding(ViewGroup parent) {
    CardBusStopBinding binding = DataBindingUtil
      .inflate(LayoutInflater.from(parent.getContext()), R.layout.card_bus_stop, parent, false,
        dataBindingComponent);
    binding.getRoot().setOnClickListener(v -> {
      StopPoint stopPoint = binding.getStopPoint();
      if (stopPoint != null && busStopClickCallback != null) {
        busStopClickCallback.onClick(stopPoint);
      }
    });
    return binding;
  }

  @Override
  protected void bind(CardBusStopBinding binding, StopPoint item) {
    Timber.d("Bind : %s", item.getStopName());
    binding.setStopPoint(item);
  }

  @Override
  protected boolean areItemsTheSame(StopPoint oldItem, StopPoint newItem) {
    return Objects.equals(oldItem.getStopId(), newItem.getStopId());
  }

  @Override
  protected boolean areContentsTheSame(StopPoint oldItem, StopPoint newItem) {
    return Objects.equals(oldItem.getStopName(), newItem.getStopName());
  }

  public interface busStopClickCallback {
    void onClick(StopPoint stopPoint);
  }
}
