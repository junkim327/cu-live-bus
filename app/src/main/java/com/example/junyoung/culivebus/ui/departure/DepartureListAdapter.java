package com.example.junyoung.culivebus.ui.departure;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.databinding.DepartureCardBinding;
import com.example.junyoung.culivebus.vo.SortedDeparture;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.ZonedDateTime;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class DepartureListAdapter
  extends RecyclerView.Adapter<DepartureListAdapter.DepartureListViewHolder> {
  private static final Long ONE_SECOND = 1_000L;
  @Nullable
  private List<SortedDeparture> sortedDepartures;
  private LifecycleOwner lifecycleOwner;
  private DepartureViewModel departureViewModel;
  private int dataVersion = 0;

  public DepartureListAdapter(LifecycleOwner lifecycleOwner,
                              DepartureViewModel departureViewModel) {
    this.lifecycleOwner = lifecycleOwner;
    this.departureViewModel = departureViewModel;
  }

  @Override
  public void onViewRecycled(@NonNull DepartureListViewHolder holder) {
    Timber.d("%s : onViewRecycled called", holder.randomId);
    super.onViewRecycled(holder);

  }

  @Override
  public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    Timber.d("onDetachedFromRecyclerView called");
    super.onDetachedFromRecyclerView(recyclerView);
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull DepartureListViewHolder holder) {
    Timber.d("ID : %s, onViewDetachedFromWindow called", holder.randomId);
    super.onViewDetachedFromWindow(holder);

    if (holder.timer1 != null) {
      holder.timer1.cancel();
      holder.timer1 = null;
    }

    if (holder.timer2 != null) {
      holder.timer2.cancel();
      holder.timer2 = null;
    }
  }

  @NonNull
  @Override
  public DepartureListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    DepartureCardBinding binding =
      DepartureCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    binding.getRoot().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        SortedDeparture sortedDeparture = binding.getDeparture();
        if (sortedDeparture != null) {
          departureViewModel.departureClicked(sortedDeparture);
        }
      }
    });

    return new DepartureListViewHolder(binding, departureViewModel, lifecycleOwner);
  }

  @Override
  public void onBindViewHolder(@NonNull DepartureListViewHolder holder, int position) {
    Timber.d("onBindViewHolder is called");
    holder.bind(sortedDepartures.get(position));
  }

  @SuppressLint("StaticFieldLeak")
  @MainThread
  public void replace(List<SortedDeparture> update) {
    dataVersion++;
    if (sortedDepartures == null) {
      if (update == null) {
        return;
      }
      sortedDepartures = update;
      notifyDataSetChanged();
    } else if (update == null) {
      int oldSize = sortedDepartures.size();
      sortedDepartures = null;
      notifyItemRangeRemoved(0, oldSize);
    } else {
      final int startVersion = dataVersion;
      final List<SortedDeparture> oldItems = sortedDepartures;
      DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
        @Override
        public int getOldListSize() {
          return oldItems.size();
        }

        @Override
        public int getNewListSize() {
          return update.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
          SortedDeparture oldItem = oldItems.get(oldItemPosition);
          SortedDeparture newItem = update.get(newItemPosition);
          return Objects.equals(
            oldItem.getVehicleIdList().get(0),
            newItem.getVehicleIdList().get(0)
          );
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
          SortedDeparture oldItem = oldItems.get(oldItemPosition);
          SortedDeparture newItem = update.get(newItemPosition);
          return Objects.equals(
            oldItem.getExpectedList().get(0),
            newItem.getExpectedList().get(0)
          );
        }
      });

      if (startVersion != dataVersion) {
        // ignore update
        return;
      }
      sortedDepartures = update;
      diffResult.dispatchUpdatesTo(DepartureListAdapter.this);
    }
  }

  @Override
  public int getItemCount() {
    return sortedDepartures == null ? 0 : sortedDepartures.size();
  }

  public static class DepartureListViewHolder extends RecyclerView.ViewHolder {
    public final DepartureCardBinding binding;
    private final LifecycleOwner lifecycleOwner;
    private final DepartureViewModel departureViewModel;
    private final MutableLiveData<Duration> duration1 = new MutableLiveData<>();
    private final MutableLiveData<Duration> duration2 = new MutableLiveData<>();
    private CountDownTimer timer1;
    private CountDownTimer timer2;
    private final int randomId = ThreadLocalRandom.current().nextInt(0, 1000);

    DepartureListViewHolder(DepartureCardBinding binding, DepartureViewModel departureViewModel,
                            LifecycleOwner lifecycleOwner) {
      super(binding.getRoot());
      this.binding = binding;
      this.lifecycleOwner = lifecycleOwner;
      this.departureViewModel = departureViewModel;
    }

    public void bind(SortedDeparture sortedDeparture) {
      Timber.d("ViewHolder ID : %s, bind() is called", randomId);
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
        Timber.d("Seconds: %s", timeUntilDeparture.getSeconds());
        long millisInFuture = timeUntilDeparture.plusSeconds(20).getSeconds() * ONE_SECOND;
        timer1 = new CountDownTimer(millisInFuture, ONE_SECOND) {
          @Override
          public void onTick(long l) {
            //Timber.d("%s : Tick", l / 1000);
            duration1.setValue(binding.getDuration1().getValue().minusMillis(ONE_SECOND));
          }

          @Override
          public void onFinish() {
            departureViewModel.resetTimer();
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
