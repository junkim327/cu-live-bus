package com.example.junyoung.culivebus.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.junyoung.culivebus.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.res.ResourcesCompat;

public class CustomDimDialog extends AppCompatDialog {
  public CustomDimDialog(@Nullable Context context) {
    super(context, R.style.Theme_CuLiveBus_Dialog);
    this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Window window = this.getWindow();
    if (window != null) {
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
  }

  @Override
  public void setContentView(View view) {
    if (view != null) {
      super.setContentView(wrap(view));
    }
  }

  private View wrap(View content) {
    Context context = this.getContext();
    Resources resources = context.getResources();
    int verticalMargin = resources.getDimensionPixelSize(R.dimen.margin_dialog_vertical);
    int horizontalMargin = resources.getDimensionPixelSize(R.dimen.margin_dialog_horizontal);

    FrameLayout frameLayout = new FrameLayout(context);
    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT,
      FrameLayout.LayoutParams.WRAP_CONTENT
    );
    layoutParams.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
    layoutParams.gravity = Gravity.CENTER;
    frameLayout.addView(content, layoutParams);

    Rect rect = new Rect();
    frameLayout.setOnTouchListener((view, motionEvent) -> {
      boolean hasConsumed;
      switch (motionEvent.getAction()) {
        case MotionEvent.ACTION_DOWN:
          content.getGlobalVisibleRect(rect);
          if (!rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            cancel();
            hasConsumed = true;
          } else {
            hasConsumed =  false;
          }
          break;
        default:
          hasConsumed = false;
      }

      return hasConsumed;
    });

    frameLayout.setBackground(new ColorDrawable(
      ResourcesCompat.getColor(resources, R.color.scrim, frameLayout.getContext().getTheme())));

    return frameLayout;
  }
}
