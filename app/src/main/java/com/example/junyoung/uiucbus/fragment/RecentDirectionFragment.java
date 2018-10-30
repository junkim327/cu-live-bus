package com.example.junyoung.uiucbus.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.junyoung.uiucbus.OnInternetConnectedListener;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.RecyclerviewClickListener;
import com.example.junyoung.uiucbus.adapter.RecentDirectionAdapter;
import com.example.junyoung.uiucbus.room.entity.RouteInfo;
import com.example.junyoung.uiucbus.ui.factory.DirectionViewModelFactory;
import com.example.junyoung.uiucbus.ui.Injection;
import com.example.junyoung.uiucbus.ui.viewmodel.RouteInfoViewModel;
import com.example.junyoung.uiucbus.util.UtilConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class RecentDirectionFragment extends Fragment {
  private static final String TAG = RecentDirectionFragment.class.getSimpleName();

  private String mUid;

  private Unbinder mUnbinder;
  private RecentDirectionAdapter mAdapter;
  private ConnectivityManager mConnectivityManager;
  private OnInternetConnectedListener mInternetConnectedCallback;
  private OnRecentDirectionClickedkListener mRecentDirectionCallback;
  private RouteInfoViewModel mViewModel;
  private DirectionViewModelFactory mViewModelFactory;
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  @BindView(R.id.text_header_recent_direction)
  TextView mHeaderTextView;
  @BindView(R.id.recyclerview_recent_direction)
  RecyclerView mRecyclerview;
  @BindView(R.id.button_view_more_directions_recent_direction)
  Button mViewMoreDirectionButton;

  public interface OnRecentDirectionClickedkListener {
    void onRecentDirectionClick(RouteInfo directionInfo);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    try {
      mInternetConnectedCallback = (OnInternetConnectedListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnInternetConnectedListener.");
    }

    try {
      mRecentDirectionCallback = (OnRecentDirectionClickedkListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnRecentDirectionClickListener."
      );
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "onCreate is called");

    mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(
      Context.CONNECTIVITY_SERVICE
    );

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );

    // Get uid from shared preferences. If there is no saved uid, then return null.
    mUid = sharedPref.getString(getString(R.string.saved_uid), null);
    Log.d(TAG, "you think this line is called?");

    mViewModelFactory = Injection.provideDirectionViewModelFactory(getContext());
    mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(RouteInfoViewModel.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (mConnectivityManager != null) {
      UtilConnection.isInternetConnected(mConnectivityManager, mInternetConnectedCallback, true);
    }

    View view = inflater.inflate(R.layout.fragment_recent_direction, container, false);
    mUnbinder = ButterKnife.bind(this, view);

    Log.d(TAG, "onCreateView is called");

    setRecyclerView();

    return view;
  }

  public void setRecyclerView() {
    mRecyclerview.setHasFixedSize(true);
    mRecyclerview.setNestedScrollingEnabled(false);

    LayoutManager layoutManager = new LinearLayoutManager(getContext());
    mRecyclerview.setLayoutManager(layoutManager);

    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
      mRecyclerview.getContext(),
      DividerItemDecoration.VERTICAL
    ) {
      @Override
      public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == parent.getAdapter().getItemCount() - 1) {
          outRect.setEmpty();
        } else {
          super.getItemOffsets(outRect, view, parent, state);
        }
      }
    };
    mRecyclerview.addItemDecoration(dividerItemDecoration);

    RecyclerviewClickListener listener = (view, position) -> {
      boolean isConnected = true;
      if (mConnectivityManager != null) {
        isConnected = UtilConnection.isInternetConnected(mConnectivityManager,
          mInternetConnectedCallback,
          true);
      }

      if (mAdapter != null && isConnected) {
        RouteInfo selectedDirectionInfo = mAdapter.getDirectionList().get(position);
        mRecentDirectionCallback.onRecentDirectionClick(selectedDirectionInfo);
      }
    };

    mAdapter = new RecentDirectionAdapter(getContext(), listener);
    mRecyclerview.setAdapter(mAdapter);
  }

  @Override
  public void onStart() {
    super.onStart();

    // If uid is not null, then retrieve user's recent searched directions from database.
    // If there is no uid, then do nothing.
    if (mUid != null) {
      mDisposable.add(mViewModel.getRouteInfoListByUid(mUid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(routeInfos -> {
            if (routeInfos != null) {
              Log.d(TAG, "Total Route Info: " + routeInfos.size());
              Log.d(TAG, "UID: " + mUid);
              mAdapter.setDirectionList(routeInfos);
              if (routeInfos.size() > 0) {
                mHeaderTextView.setVisibility(VISIBLE);
              }
              if (routeInfos.size() > 7) {
                mViewMoreDirectionButton.setVisibility(VISIBLE);
              }
            }
          },
          throwable -> {
          Log.e(TAG, "Unable to update recent directions", throwable);
        }));
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mConnectivityManager != null) {
      UtilConnection.isInternetConnected(mConnectivityManager, mInternetConnectedCallback, true);
    }
  }

  @OnClick(R.id.button_view_more_directions_recent_direction)
  public void expandRecentDirectionList() {
    mViewMoreDirectionButton.setClickable(false);
    mViewMoreDirectionButton.setVisibility(GONE);
    mAdapter.setExpanded(true);
  }

  @Override
  public void onStop() {
    super.onStop();

    mDisposable.clear();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    mUnbinder.unbind();
  }
}
