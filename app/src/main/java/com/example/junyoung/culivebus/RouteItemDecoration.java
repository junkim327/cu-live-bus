package com.example.junyoung.culivebus;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.State;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.junyoung.culivebus.adapter.BusRouteAdapter;

public class RouteItemDecoration extends RecyclerView.ItemDecoration {
  public static final String TAG = "RouteItemDecoration";
  private static final float STROKE_WIDTH = 16.0f;

  private Context context;
  private Paint routePaint;

  public RouteItemDecoration(Context context, String busColor) {
    this.context = context;
    routePaint = new Paint();
    routePaint.setColor(Color.parseColor(busColor));
    routePaint.setStyle(Paint.Style.FILL);
    routePaint.setStrokeWidth(STROKE_WIDTH);
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, @NonNull State state) {
    super.getItemOffsets(outRect, view, parent, state);
    outRect.set(0, 0, 0, 0);
  }

  @Override
  public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull State state) {
    super.onDrawOver(c, parent, state);

    int totalCount = parent.getAdapter().getItemCount();
    int childCount = parent.getChildCount();
    Log.d(TAG, "Child count : " + childCount);
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      int adapterPosition = parent.getChildAdapterPosition(child);
      //Log.d("RouteItemDecoration", "Adapter Position : " + adapterPosition);
      int viewType = parent.getAdapter().getItemViewType(adapterPosition);

      int cardTop = child.getTop();
      int cardBottom = child.getBottom();
      int cardHeight = child.getHeight() * i;
      float cardVerticalCenter = (cardTop + cardBottom) / 2;

      if (viewType == BusRouteAdapter.ROUTE) {
        TextView busStopNameTextView =
          child.findViewById(R.id.textview_bus_stop_name_bus_routes_card);
        TextView busStopCodeTextView =
          child.findViewById(R.id.textview_bus_stop_code_bus_routes_card);
        float routeLeft = busStopNameTextView.getLeft()
          - DeviceDimensionsHelper.convertDpToPixel(32, context);
        float markerLeft = routeLeft - DeviceDimensionsHelper.convertDpToPixel(8, context)
          - DeviceDimensionsHelper.convertPixelsToDp(12, context);
        float markerTop = cardVerticalCenter - DeviceDimensionsHelper.convertDpToPixel(8, context);

        if (adapterPosition == 2) {
          c.drawLine(routeLeft, cardVerticalCenter, routeLeft, cardBottom, routePaint);
        } else if (adapterPosition == totalCount - 1) {
          c.drawLine(routeLeft, cardTop, routeLeft, cardVerticalCenter, routePaint);
        } else {
          c.drawLine(routeLeft, cardTop, routeLeft, cardBottom, routePaint);
        }

        Paint circlePaint = new Paint();
        circlePaint.setColor(context.getResources().getColor(R.color.colorPrimary));
        c.drawCircle(routeLeft, cardVerticalCenter, 40, circlePaint);
        Paint innerCirclePaint = new Paint();
        innerCirclePaint.setColor(context.getResources().getColor(R.color.white));
        c.drawCircle(routeLeft, cardVerticalCenter, 30, innerCirclePaint);
        /*
        c.drawBitmap(
          BitmapFactory.decodeResource(context.getResources(), R.drawable.route_marker),
          markerLeft,
          markerTop,
          routePaint
        );*/
      }
    }
  }
}
