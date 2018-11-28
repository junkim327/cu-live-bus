package com.example.junyoung.culivebus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.httpclient.pojos.StopTimes;
import com.example.junyoung.culivebus.util.listener.RecyclerviewClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusRouteAdapter extends Adapter<ViewHolder> {
  private static final int HEADER = 1;
  private static final int BUSINFO = 2;
  public static final int ROUTE = 3;

  private Context mContext;
  private String mBusColor;
  private String mBusStopName;
  private String mBusStopCode;
  private List<StopTimes> mStopTimesList = new ArrayList<>();

  public static class BottomSheetHeaderViewHolder extends ViewHolder {
    public ImageView mDragImageView;

    public BottomSheetHeaderViewHolder(View itemView) {
      super(itemView);

      mDragImageView = itemView.findViewById(R.id.image_drag_card_bottom_sheet_header);
    }
  }

  static class BusInfoViewHolder extends ViewHolder {
    @BindView(R.id.text_bus_name_card_bus_info)
    TextView mBusNameTextView;
    @BindView(R.id.text_bus_code_card_bus_info)
    TextView mBusCodeTextView;

    public BusInfoViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }
  }

  public static class BusRouteViewHolder extends ViewHolder
    implements View.OnClickListener {
    public ImageView flagIconImageView;
    public TextView busStopCodeTextView;
    public TextView busStopNameTextView;
    public RecyclerviewClickListener mListener;

    public BusRouteViewHolder(View itemView, RecyclerviewClickListener listener) {
      super(itemView);

      mListener = listener;
      flagIconImageView = itemView.findViewById(R.id.image_flag_icon_card_bus_routes);
      busStopCodeTextView = itemView.findViewById(R.id.textview_bus_stop_code_bus_routes_card);
      busStopNameTextView = itemView.findViewById(R.id.textview_bus_stop_name_bus_routes_card);

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      int pos = getAdapterPosition();
      if (pos != RecyclerView.NO_POSITION) {
        mListener.onClick(view, pos);
      }
    }
  }

  private RecyclerviewClickListener mListener;

  public BusRouteAdapter(Context context, RecyclerviewClickListener listener) {
    mContext = context;
    mListener = listener;
  }

  public void updateBusInfo(String busName, String busCode) {

  }

  public void updateStopTimesList(String busColor,
                                  String busStopName,
                                  String busStopCode,
                                  List<StopTimes> stopTimesList) {
    mBusColor = busColor;
    mBusStopName = busStopName;
    mBusStopCode = busStopCode;
    mStopTimesList.clear();
    mStopTimesList.addAll(stopTimesList);
    notifyDataSetChanged();
  }

  public StopTimes getClickedStop(int position) {
    if (mStopTimesList != null && !(position < 0 || position >= mStopTimesList.size())) {
      return mStopTimesList.get(position);
    }

    return null;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ViewHolder viewHolder;
    LayoutInflater inflater = LayoutInflater.from(mContext);

    switch (viewType) {
      case HEADER:
        View headerView = inflater.inflate(R.layout.card_bottom_sheet_header, parent, false);
        viewHolder = new BottomSheetHeaderViewHolder(headerView);
        break;
      case BUSINFO:
        View busInfoView = inflater.inflate(R.layout.card_bus_info, parent, false);
        viewHolder = new BusInfoViewHolder(busInfoView);
        break;
      default:
        View busRoutesView = inflater.inflate(R.layout.card_bus_routes, parent, false);
        viewHolder = new BusRouteViewHolder(busRoutesView, mListener);
        break;
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    if (holder.getItemViewType() == BUSINFO) {
      configureBusInfoViewHolder((BusInfoViewHolder) holder);
    } else if (holder.getItemViewType() == ROUTE) {
      configureRouteViewHolder((BusRouteViewHolder) holder, position - 2);
    }
  }

  private void configureBusInfoViewHolder(BusInfoViewHolder holder) {
    if (mBusStopName != null && mBusStopCode != null) {
      holder.mBusNameTextView.setText(mBusStopName);
      holder.mBusCodeTextView.setText(mBusStopCode);
    }
  }

  private void configureRouteViewHolder(BusRouteViewHolder holder, int position) {
    holder.busStopCodeTextView.setText(mStopTimesList.get(position).getStopPoint().getStopCode());
    holder.busStopNameTextView.setText(mStopTimesList.get(position).getStopPoint().getStopName());
  }

  @Override
  public int getItemCount() {
    return mStopTimesList == null ? 0 : (mStopTimesList.size() + 2);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return HEADER;
    } else if (position == 1) {
      return BUSINFO;
    } else {
      return ROUTE;
    }
  }
}
