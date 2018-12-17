package com.example.junyoung.culivebus.ui.download;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.di.Injectable;
import com.example.junyoung.culivebus.ui.common.NavigationController;
import com.example.junyoung.culivebus.vo.Status;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import timber.log.Timber;

public class DownloadFragment extends Fragment implements Injectable {
  @Inject
  ViewModelProvider.Factory viewModelFactory;
  @Inject
  NavigationController navigationController;

  private DownloadViewModel downloadViewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_download, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    downloadViewModel = ViewModelProviders.of(this, viewModelFactory).get(DownloadViewModel.class);
    downloadViewModel.getNumStops().observe(this, numStops -> {
      if (numStops.status == Status.SUCCESS) {
        Timber.d("Num stops: %d", numStops.data);
      }
    });
  }
}
