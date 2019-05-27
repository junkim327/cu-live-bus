package com.example.junyoung.culivebus.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.db.entity.SearchedPlace;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;

import java.util.List;

public class RecentPlaceAdapter
  extends RecyclerView.Adapter<RecentPlaceAdapter.RecentPlaceViewHolder> {
  public static class RecentPlaceViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
    public TextView mPlaceNameTextView;
    public RecyclerviewClickListener mRecyclerviewClickListener;

    public RecentPlaceViewHolder(View itemView, RecyclerviewClickListener listener) {
      super(itemView);

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
  private List<SearchedPlace> mSearchedPlaceList;
  private RecyclerviewClickListener mRecyclerviewClickListener;

  public RecentPlaceAdapter(Context context, RecyclerviewClickListener listener) {
    mContext = context;
    mRecyclerviewClickListener = listener;
  }

  public void setPlaceList(List<SearchedPlace> searchedPlaceList) {
    mSearchedPlaceList = searchedPlaceList;
    notifyDataSetChanged();
  }

  public SearchedPlace getPlace(int position) {
    if (mSearchedPlaceList != null) {
      return mSearchedPlaceList.get(position);
    }

    return null;
  }

  @NonNull
  @Override
  public RecentPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);

    View placeView = inflater.inflate(R.layout.card_searched_place, parent, false);

    RecentPlaceViewHolder vh = new RecentPlaceViewHolder(
      placeView,
      mRecyclerviewClickListener
    );

    return vh;
  }

  @Override
  public void onBindViewHolder(@NonNull RecentPlaceViewHolder holder, int position) {
    if (mSearchedPlaceList != null && mSearchedPlaceList.size() != 0) {
      holder.mPlaceNameTextView.setText(mSearchedPlaceList.get(position).getPlaceName());
    }
  }

  @Override
  public int getItemCount() {
    if (mSearchedPlaceList != null) {
      return mSearchedPlaceList.size();
    } else {
      return 0;
    }
  }
}
