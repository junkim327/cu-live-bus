package com.example.junyoung.culivebus.ui.route;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.State;

import android.view.View;
import android.widget.TextView;

import com.example.junyoung.culivebus.DeviceDimensionsHelper;
import com.example.junyoung.culivebus.R;

public class RouteItemDecoration extends RecyclerView.ItemDecoration {
  public static final String TAG = "RouteItemDecoration";
  private static final float STROKE_WIDTH = 16.0f;

  private Context context;
  private Paint mRoutePaint;
  private int mBusColor;

  public RouteItemDecoration(Context context, String busColor) {
    this.context = context;
    mRoutePaint = new Paint();
    mBusColor = Color.parseColor("#" + busColor);
    mRoutePaint.setColor(mBusColor);
    mRoutePaint.setStyle(Paint.Style.FILL);
    mRoutePaint.setStrokeWidth(STROKE_WIDTH);
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

    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      int adapterPosition = parent.getChildAdapterPosition(child);
      //Log.d("RouteItemDecoration", "Adapter Position : " + adapterPosition);
      int viewType = parent.getAdapter().getItemViewType(adapterPosition);

      int cardTop = child.getTop();
      int cardBottom = child.getBottom();
      int cardHeight = child.getHeight() * i;
      float cardVerticalCenter = (cardTop + cardBottom) / 2;

      if (viewType == R.layout.card_bottom_sheet_route) {
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
          c.drawLine(routeLeft, cardVerticalCenter, routeLeft, cardBottom, mRoutePaint);
        } else if (adapterPosition == totalCount - 1) {
          c.drawLine(routeLeft, cardTop, routeLeft, cardVerticalCenter, mRoutePaint);
        } else {
          c.drawLine(routeLeft, cardTop, routeLeft, cardBottom, mRoutePaint);
        }

        Paint circlePaint = new Paint();
        circlePaint.setColor(mBusColor);
        c.drawCircle(routeLeft, cardVerticalCenter, 40, circlePaint);
        Paint innerCirclePaint = new Paint();
        innerCirclePaint.setColor(context.getResources().getColor(R.color.white));
        c.drawCircle(routeLeft, cardVerticalCenter, 30, innerCirclePaint);
        /*
        c.drawBitmap(
          BitmapFactory.decodeResource(context.getResources(), R.drawable.route_marker),
          markerLeft,
          markerTop,
          mRoutePaint
        );*/
      }
    }
  }
}
