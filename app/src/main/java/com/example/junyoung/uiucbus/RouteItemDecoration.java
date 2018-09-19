package com.example.junyoung.uiucbus;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class RouteItemDecoration extends RecyclerView.ItemDecoration {
  public static final String TAG = "RouteItemDecoration";

  private Context context;
  private Paint routePaint;
  private float strokeWidth = 12.0f;

  public RouteItemDecoration(Context context, String busColor) {
    this.context = context;
    routePaint = new Paint();
    routePaint.setColor(Color.parseColor(busColor));
    routePaint.setStyle(Paint.Style.FILL);
    routePaint.setStrokeWidth(strokeWidth);
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    outRect.set(0, 0, 0, 0);
  }

  @Override
  public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
    super.onDrawOver(c, parent, state);

    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      View child = parent.getChildAt(i);
      LayoutParams params = (LayoutParams) child.getLayoutParams();

      int cardTop = child.getTop();
      int cardBottom = child.getBottom();
      int cardHeight = child.getHeight() * i;
      float cardVerticalCenter = (cardTop + cardBottom) / 2;

      TextView busStopNameTextView =
        child.findViewById(R.id.textview_bus_stop_name_bus_routes_card);
      TextView busStopCodeTextView =
        child.findViewById(R.id.textview_bus_stop_code_bus_routes_card);
      float routeLeft =busStopNameTextView.getLeft()
        - DeviceDimensionsHelper.convertDpToPixel(16, context);
      float markerLeft = routeLeft - DeviceDimensionsHelper.convertDpToPixel(8, context)
        - DeviceDimensionsHelper.convertPixelsToDp(12, context);
      float markerTop = cardVerticalCenter - DeviceDimensionsHelper.convertDpToPixel(8, context);


      int cardPosition = params.getViewAdapterPosition();
      if (cardPosition == 0) {
        c.drawLine(routeLeft, cardVerticalCenter, routeLeft, cardBottom, routePaint);
      } else if (cardPosition == parent.getAdapter().getItemCount() - 1) {
        c.drawLine(routeLeft, cardTop, routeLeft, cardVerticalCenter, routePaint);
      } else {
        c.drawLine(routeLeft, cardTop, routeLeft, cardBottom, routePaint);
      }
      c.drawBitmap(
        BitmapFactory.decodeResource(context.getResources(), R.drawable.route_marker),
        markerLeft,
        markerTop,
        routePaint
      );
    }
  }
}
