package com.example.junyoung.culivebus.ui.nearbystop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.example.junyoung.culivebus.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class BusStopInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
  private Context mContext;

  public BusStopInfoWindowAdapter(Context context) {
    mContext = context;
  }

  @Override
  public View getInfoWindow(Marker marker) {
    ImageView windowImage = new ImageView(mContext);
    windowImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_bus_stop_color));

    return windowImage;
  }

  @Override
  public View getInfoContents(Marker marker) {
    return null;
  }
}
