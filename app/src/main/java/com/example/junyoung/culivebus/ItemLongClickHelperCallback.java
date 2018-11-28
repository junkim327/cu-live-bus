package com.example.junyoung.culivebus;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.example.junyoung.culivebus.adapter.EditFavoritesAdapter;
import com.example.junyoung.culivebus.adapter.ItemLongClickHelperAdapter;

public class ItemLongClickHelperCallback extends ItemTouchHelper.Callback {
  private final ItemLongClickHelperAdapter adapter;

  public ItemLongClickHelperCallback(EditFavoritesAdapter adapter) {
    this.adapter = adapter;
  }

  @Override
  public boolean isLongPressDragEnabled() {
    return true;
  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    return false;
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    int swipeFlags = 0;

    return makeMovementFlags(dragFlags, swipeFlags);
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
    adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    Log.d("onMove", " is called multipletimes");

    return true;
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

  }
}
