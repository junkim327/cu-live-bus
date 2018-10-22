package com.example.junyoung.uiucbus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.RecyclerviewClickListener;
import com.example.junyoung.uiucbus.room.entity.UserPlace;

import java.util.List;

public class RecentPlaceAdapter
  extends RecyclerView.Adapter<RecentPlaceAdapter.RecentPlaceViewHolder> {
  public static class RecentPlaceViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
    public TextView mPlaceNameTextView;
    public RecyclerviewClickListener mRecyclerviewClickListener;

    public RecentPlaceViewHolder(View itemView, RecyclerviewClickListener listener) {
      super(itemView);

      mPlaceNameTextView = itemView.findViewById(R.id.text_place_name_card_recent_place);
      mRecyclerviewClickListener = listener;
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      int pos = getAdapterPosition();
      if (pos != RecyclerView.NO_POSITION) {
        mRecyclerviewClickListener.onClick(view, pos);
      }
    }
  }

  private Context mContext;
  private List<UserPlace> mUserPlaceList;
  private RecyclerviewClickListener mRecyclerviewClickListener;

  public RecentPlaceAdapter(Context context, RecyclerviewClickListener listener) {
    mContext = context;
    mRecyclerviewClickListener = listener;
  }

  public void setPlaceList(List<UserPlace> userPlaceList) {
    mUserPlaceList = userPlaceList;
    notifyDataSetChanged();
  }

  public UserPlace getPlace(int position) {
    if (mUserPlaceList != null) {
      return mUserPlaceList.get(position);
    }

    return null;
  }

  @NonNull
  @Override
  public RecentPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);

    View placeView = inflater.inflate(R.layout.card_recent_place, parent, false);

    RecentPlaceViewHolder vh = new RecentPlaceViewHolder(
      placeView,
      mRecyclerviewClickListener
    );

    return vh;
  }

  @Override
  public void onBindViewHolder(@NonNull RecentPlaceViewHolder holder, int position) {
    if (mUserPlaceList != null && mUserPlaceList.size() != 0) {
      holder.mPlaceNameTextView.setText(mUserPlaceList.get(position).getPlaceName());
    }
  }

  @Override
  public int getItemCount() {
    if (mUserPlaceList != null) {
      return mUserPlaceList.size();
    } else {
      return 0;
    }
  }
}
