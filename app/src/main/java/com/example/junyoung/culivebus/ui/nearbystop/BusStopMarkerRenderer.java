package com.example.junyoung.culivebus.ui.nearbystop;

import android.content.Context;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.db.entity.StopPoint;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class BusStopMarkerRenderer extends DefaultClusterRenderer<StopPoint> {
  public BusStopMarkerRenderer(Context context, GoogleMap map,
                               ClusterManager<StopPoint> clusterManager) {
    super(context, map, clusterManager);
  }

  @Override
  protected void onBeforeClusterItemRendered(StopPoint item, MarkerOptions markerOptions) {
    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.google_map_custom_marker));
  }

  @Override
  protected boolean shouldRenderAsCluster(Cluster<StopPoint> cluster) {
    return super.shouldRenderAsCluster(cluster);
  }
}
