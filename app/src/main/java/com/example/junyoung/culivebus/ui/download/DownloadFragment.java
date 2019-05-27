package com.example.junyoung.culivebus.ui.download;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.junyoung.culivebus.ConnectivityLiveData;
import com.example.junyoung.culivebus.Constants;
import com.example.junyoung.culivebus.MainActivity;
import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.vo.Resource;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerFragment;
import timber.log.Timber;

public class DownloadFragment extends DaggerFragment {
  @Inject
  ViewModelProvider.Factory viewModelFactory;

  private DownloadViewModel downloadViewModel;
  private ConstraintLayout constraintLayout;
  private TextView downloadStatus;
  private LottieAnimationView lottieAnimationView;

  private boolean isFirstTimeDownloaded;
  private boolean isDataDownloaded;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    isFirstTimeDownloaded = true;
    isDataDownloaded = false;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_download, container, false);

    constraintLayout = view.findViewById(R.id.constraintLayout_download);
    downloadStatus = view.findViewById(R.id.downloadStatusText);
    lottieAnimationView = view.findViewById(R.id.animationView);
    lottieAnimationView.setScale(0.5f);
    view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
      }
    });

    return view;
  }

  private void animateToDownloadTwo() {
    ConstraintSet constraintSet2 = new ConstraintSet();
    constraintSet2.clone(getActivity(), R.layout.fragment_download2);
    TransitionManager.beginDelayedTransition(constraintLayout);
    constraintSet2.applyTo(constraintLayout);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    /*
    downloadViewModel = ViewModelProviders.of(this, viewModelFactory).get(DownloadViewModel.class);
    downloadViewModel.getNumStops().observe(this, numStops -> {
      if (numStops.status == Status.SUCCESS) {
        Timber.d("Num stops: %d", numStops.data);
      }
    });*/
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    downloadViewModel = ViewModelProviders.of(this, viewModelFactory).get(DownloadViewModel.class);
    downloadViewModel.getNumStops().observe(getViewLifecycleOwner(), new Observer<Resource<Integer>>() {
      @Override
      public void onChanged(Resource<Integer> resource) {
        Timber.d("Download status : %s", resource.status);
        switch (resource.status) {
          case LOADING:
            break;
          case SUCCESS:
            if (resource.data != null && resource.data > 0) {
              isDataDownloaded = true;
              downloadViewModel.saveOnboardingCompletedResult(true);
              Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  downloadStatus.setText(getResources().getString(R.string.status_complete));
                  animateToDownloadTwo();
                }
              }, 2000);
            }
            break;
          case ERROR:
            downloadStatus.setText(getResources().getString(R.string.status_fail));
            downloadViewModel.refresh();
            break;
        }
      }
    });

    monitorInternetConnectivity();
  }

  private void monitorInternetConnectivity() {
    ConnectivityManager cm =
      (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    ConnectivityLiveData connectivityLiveData = new ConnectivityLiveData(cm);
    connectivityLiveData.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
      @Override
      public void onChanged(Boolean isConnected) {
        Timber.d("isConnected : %s", isConnected);
        if (!isDataDownloaded) {
          if (isConnected) {
            if (isFirstTimeDownloaded) {
              downloadViewModel.setApiKey(Constants.API_KEY);
            } else {
              downloadViewModel.refresh();
            }
            downloadStatus.setText(getString(R.string.status_downloading));
            lottieAnimationView.playAnimation();
          } else {
            showNoConnectionToast();
            downloadStatus.setText(getString(R.string.status_preparing));
            lottieAnimationView.pauseAnimation();
          }
        }
      }
    });
  }

  private void showNoConnectionToast() {
    String message = getResources().getString(R.string.no_internet_connection_message);

    Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
    toast.show();
  }
}
