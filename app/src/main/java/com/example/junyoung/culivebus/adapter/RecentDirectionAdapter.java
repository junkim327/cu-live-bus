package com.example.junyoung.culivebus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;
import com.example.junyoung.culivebus.room.entity.RouteInfo;

import java.util.List;

public class RecentDirectionAdapter
  extends RecyclerView.Adapter<RecentDirectionAdapter.RecentDirectionViewHolder> {
  public static class RecentDirectionViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
    public TextView mStartingPointNameTextView;
    public TextView mDestinationNameTextView;
    public RecyclerviewClickListener mRecyclerviewClickListener;

    public RecentDirectionViewHolder(View itemView, RecyclerviewClickListener listener) {
      super(itemView);

      mStartingPointNameTextView =
        itemView.findViewById(R.id.text_starting_point_name_card_recent_direction);
      mDestinationNameTextView =
        itemView.findViewById(R.id.text_destination_name_card_recent_direction);
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

  private boolean mExpanded;
  private Context mContext;
  private List<RouteInfo> mDirectionList;
  private RecyclerviewClickListener mRecyclerViewClickListener;

  public RecentDirectionAdapter(Context context, RecyclerviewClickListener listener) {
    mExpanded = false;
    mContext = context;
    mRecyclerViewClickListener = listener;
  }

  public void setDirectionList(List<RouteInfo> directionList) {
    mDirectionList = directionList;
    notifyDataSetChanged();
  }

  public List<RouteInfo> getDirectionList() {
    return mDirectionList;
  }

  public void setExpanded(boolean expanded) {
    mExpanded = expanded;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public RecentDirectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);

    View directionView = inflater.inflate(R.layout.card_recent_direction, parent, false);

    RecentDirectionViewHolder viewHolder = new RecentDirectionViewHolder(
      directionView,
      mRecyclerViewClickListener
    );

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull RecentDirectionViewHolder holder, int position) {
    if (mDirectionList != null && mDirectionList.size() != 0) {
      holder.mStartingPointNameTextView.setText(
        mDirectionList.get(position).getStartingPointName()
      );
      holder.mDestinationNameTextView.setText(
        mDirectionList.get(position).getDestinationName()
      );
    }
  }

  @Override
  public int getItemCount() {
    if (mDirectionList != null) {
      if (!mExpanded && mDirectionList.size() > 7) {
        return 7;
      } else {
        return mDirectionList.size();
      }
    } else {
      return 0;
    }
  }
}
