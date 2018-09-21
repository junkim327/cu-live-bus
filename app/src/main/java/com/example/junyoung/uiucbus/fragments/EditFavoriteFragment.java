package com.example.junyoung.uiucbus.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.uiucbus.ItemLongClickHelperCallback;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.adapters.EditFavoritesAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditFavoriteFragment extends Fragment {
  @BindView(R.id.recyclerview_edit_favorites)
  RecyclerView recyclerView;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_edit_favorites, container, false);
    ButterKnife.bind(this, view);

    recyclerView.setHasFixedSize(true);
    LayoutManager layoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layoutManager);
    Adapter adapter = new EditFavoritesAdapter(getContext());
    recyclerView.setAdapter(adapter);
    ItemTouchHelper.Callback callback =
      new ItemLongClickHelperCallback((EditFavoritesAdapter) adapter);
    ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
    touchHelper.attachToRecyclerView(recyclerView);
    RecyclerView.ItemDecoration itemDecoration =
      new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
    recyclerView.addItemDecoration(itemDecoration);

    return view;
  }
}
