package com.example.junyoung.uiucbus.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.uiucbus.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NoInternetConnectionFragment extends Fragment {
  private boolean isInternetConnected;

  private Unbinder unbinder;
  private ConnectivityManager mConnectivityManager;

  @BindView(R.id.text_try_again_no_internet_connection)
  TextView tryAgainTextView;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_no_internet_connection, container, false);
    unbinder = ButterKnife.bind(this, view);

    return view;
  }

  @OnClick(R.id.text_try_again_no_internet_connection)
  public void refreshFragment() {
    if (mConnectivityManager != null) {
      NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
      isInternetConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
      if (getFragmentManager() != null && isInternetConnected) {
        getFragmentManager().popBackStackImmediate();
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
