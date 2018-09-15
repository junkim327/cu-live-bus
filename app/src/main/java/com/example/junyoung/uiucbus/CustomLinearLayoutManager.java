package com.example.junyoung.uiucbus;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class CustomLinearLayoutManager extends LinearLayoutManager {
  public CustomLinearLayoutManager(Context context) {
    super(context);
  }

  @Override
  public boolean isAutoMeasureEnabled() {
    return true;
  }
}