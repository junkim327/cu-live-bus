package com.example.junyoung.culivebus.ui.dashboard;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.databinding.DepartureCardBinding;
import com.example.junyoung.culivebus.databinding.FavoriteStopHeaderCardBinding;
import com.example.junyoung.culivebus.db.entity.FavoriteStop;
import com.example.junyoung.culivebus.vo.SortedDeparture;
import com.example.junyoung.culivebus.vo.SortedFavoriteDeparture;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.ZonedDateTime;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import timber.log.Timber;

public class DashboardListAdapter extends Adapter<ViewHolder>  {
  private static final Long ONE_SECOND = 1_000L;
  @Nullable
  private List<SortedFavoriteDeparture>  favoriteDepartures;
  private LifecycleOwner lifecycleOwner;
  private DashboardViewModel dashboardViewModel;

  public DashboardListAdapter(LifecycleOwner lifecycleOwner, DashboardViewModel dashboardViewModel) {
    this.lifecycleOwner = lifecycleOwner;
    this.dashboardViewModel = dashboardViewModel;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ViewHolder viewHolder;

    switch (viewType) {
      case R.layout.card_departure:
        viewHolder = new DepartureViewHolder(
          DepartureCardBinding.inflate(
            LayoutInflater.from(parent.getContext()),
            parent,
            false
          ),
          lifecycleOwner,
          dashboardViewModel
        );
        break;
      default:
        viewHolder = new HeaderViewHolder(
          FavoriteStopHeaderCardBinding.inflate(
            LayoutInflater.from(parent.getContext()),
            parent,
            false
          ),
          lifecycleOwner
        );
        break;
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    if (favoriteDepartures != null) {
      if (holder.getItemViewType() == R.layout.card_departure) {
        ((DepartureViewHolder) holder).bind(favoriteDepartures.get(position).getSortedDeparture());
      } else {
        ((HeaderViewHolder) holder).bind(favoriteDepartures.get(position).getFavoriteStop());
      }
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (favoriteDepartures != null && favoriteDepartures.get(position).isDeparture()) {
      return R.layout.card_departure;
    } else {
      return R.layout.card_favorite_stop_header;
    }
  }

  @Override
  public int getItemCount() {
    return favoriteDepartures == null ? 0 : favoriteDepartures.size();
  }

  public void updateFavoriteDepartures(List<SortedFavoriteDeparture> favoriteDepartures) {
    this.favoriteDepartures = favoriteDepartures;
    notifyDataSetChanged();
  }

  public static class HeaderViewHolder extends ViewHolder {
    private final FavoriteStopHeaderCardBinding binding;
    private final LifecycleOwner lifecycleOwner;

    HeaderViewHolder(FavoriteStopHeaderCardBinding binding, LifecycleOwner lifecycleOwner) {
      super(binding.getRoot());
      this.binding = binding;
      this.lifecycleOwner = lifecycleOwner;
    }

    public void bind(FavoriteStop favoriteStop) {
      binding.setStopInfo(favoriteStop);
    }
  }

  public static class DepartureViewHolder extends ViewHolder {
    private final DepartureCardBinding binding;
    private final LifecycleOwner lifecycleOwner;
    private final DashboardViewModel dashboardViewModel;
    private final MutableLiveData<Duration> duration1 = new MutableLiveData<>();
    private final MutableLiveData<Duration> duration2 = new MutableLiveData<>();
    private CountDownTimer timer1;
    private CountDownTimer timer2;

    DepartureViewHolder(DepartureCardBinding binding, LifecycleOwner lifecycleOwner,
                        DashboardViewModel dashboardViewModel) {
      super(binding.getRoot());
      this.binding = binding;
      this.lifecycleOwner = lifecycleOwner;
      this.dashboardViewModel = dashboardViewModel;
    }

    public void bind(SortedDeparture sortedDeparture) {
      binding.setDuration1(duration1);
      binding.setDuration2(duration2);
      binding.setDeparture(sortedDeparture);
      binding.setDestination(sortedDeparture.getTripList().get(0).getTripHeadSign());
      binding.setLifecycleOwner(lifecycleOwner);

      if (sortedDeparture.getExpectedList() != null) {
        if (timer1 != null) {
          timer1.cancel();
          timer1 = null;
        }

        ZonedDateTime expectedTime = ZonedDateTime.parse(sortedDeparture.getExpectedList().get(0));
        Duration timeUntilDeparture = Duration.between(Instant.now(), expectedTime);
        duration1.setValue(timeUntilDeparture);
        //Timber.d("Seconds: %s", timeUntilDeparture.getSeconds());
        long millisInFuture = timeUntilDeparture.plusSeconds(20).getSeconds() * ONE_SECOND;
        timer1 = new CountDownTimer(millisInFuture, ONE_SECOND) {
          @Override
          public void onTick(long l) {
            //Timber.d("%s : Tick", l / 1000);
            duration1.setValue(binding.getDuration1().getValue().minusMillis(ONE_SECOND));
          }

          @Override
          public void onFinish() {

          }
        }.start();

        if (timer2 != null) {
          timer2.cancel();
          timer2 = null;
        }

        if (sortedDeparture.getExpectedList().size() > 1) {
          ZonedDateTime expectedTime1 = ZonedDateTime.parse(sortedDeparture.getExpectedList().get
            (1));
          Duration timeUntilDeparture1 = Duration.between(Instant.now(), expectedTime1);
          duration2.setValue(timeUntilDeparture1);
          long millisInFuture1 = timeUntilDeparture1.plusSeconds(20).getSeconds() * ONE_SECOND;
          timer2 = new CountDownTimer(millisInFuture1, ONE_SECOND) {
            @Override
            public void onTick(long l) {
              duration2.setValue(binding.getDuration2().getValue().minusMillis(ONE_SECOND));
            }

            @Override
            public void onFinish() {

            }
          }.start();
        }
      }
    }
  }
}
