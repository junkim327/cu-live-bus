package com.example.junyoung.uiucbus.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
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

import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.RecyclerviewClickListener;
import com.example.junyoung.uiucbus.adapters.RecentDirectionAdapter;
import com.example.junyoung.uiucbus.room.entity.RouteInfo;
import com.example.junyoung.uiucbus.ui.DirectionViewModelFactory;
import com.example.junyoung.uiucbus.ui.Injection;
import com.example.junyoung.uiucbus.ui.viewmodel.RouteInfoViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RecentDirectionFragment extends Fragment {
  private static final String TAG = RecentDirectionFragment.class.getSimpleName();

  private String mUid;

  private RouteInfoViewModel mViewModel;
  private DirectionViewModelFactory mViewModelFactory;
  private RecentDirectionAdapter mAdapter;
  private RecentDirectionClickListener onRecentDirectionCallback;
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  @BindView(R.id.recyclerview_recent_direction)
  RecyclerView mRecyclerview;

  public interface RecentDirectionClickListener {
    void onRecentDirectionClick(RouteInfo directionInfo);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    try {
      onRecentDirectionCallback = (RecentDirectionClickListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnRecentDirectionClickListener."
      );
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences sharedPref = getActivity().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );

    // Get uid from shared preferences. If there is no saved uid, then return null.
    mUid = sharedPref.getString(getString(R.string.saved_uid), null);

    mViewModelFactory = Injection.provideDirectionViewModelFactory(getContext());
    mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(RouteInfoViewModel.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_recent_direction, container, false);
    ButterKnife.bind(this, view);

    setRecyclerview();

    return view;
  }

  public void setRecyclerview() {
    mRecyclerview.setHasFixedSize(true);

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
      if (mAdapter != null) {
        RouteInfo selectedDirectionInfo = mAdapter.getDirectionList().get(position);
        onRecentDirectionCallback.onRecentDirectionClick(selectedDirectionInfo);
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
            }
          },
          throwable -> Log.e(TAG, "Unable to update recent directions", throwable)));
    }
  }

  @Override
  public void onStop() {
    super.onStop();

    mDisposable.clear();
  }
}
