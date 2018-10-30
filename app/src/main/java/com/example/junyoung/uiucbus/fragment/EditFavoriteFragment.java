package com.example.junyoung.uiucbus.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.uiucbus.ItemLongClickHelperCallback;
import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.RecyclerviewClickListener;
import com.example.junyoung.uiucbus.SharedPrefUtil;
import com.example.junyoung.uiucbus.adapter.EditFavoritesAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditFavoriteFragment extends Fragment {
  private EditFavoritesAdapter adapter;

  private ArrayList<String> savedStopIds = null;
  private ArrayList<String> savedStopCodes = null;
  private ArrayList<String> savedStopNames = null;

  @BindView(R.id.recyclerview_edit_favorites)
  RecyclerView recyclerView;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences sharedPref = getContext().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );

    savedStopIds = SharedPrefUtil.getArrayList(sharedPref, getString(R.string.saved_stop_ids));
    savedStopCodes = SharedPrefUtil.getArrayList(sharedPref, getString(R.string.saved_stop_codes));
    savedStopNames = SharedPrefUtil.getArrayList(sharedPref, getString(R.string.saved_stop_names));
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_edit_favorites, container, false);
    ButterKnife.bind(this, view);

    recyclerView.setHasFixedSize(true);
    LayoutManager layoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layoutManager);
    RecyclerviewClickListener listener = (view1, position) ->
      createDialog(position);
    adapter = new EditFavoritesAdapter(getContext(), listener);
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

  private void createDialog(int position) {
    AlertDialog.Builder builder;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
    } else {
      builder = new AlertDialog.Builder(getContext());
    }
    builder.setMessage(R.string.edit_favorite_dialog_message);
    builder.setPositiveButton(R.string.ok, (dialogInterface, i)
      -> removeSavedStopsFromSharedPref(position));
    builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
    builder.show();
  }

  private void removeSavedStopsFromSharedPref(int position) {
    SharedPreferences sharedPref = getContext().getSharedPreferences(
      getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    SharedPreferences.Editor editor = sharedPref.edit();
    Gson gson = new Gson();
    String stopIdsKey = getString(R.string.saved_stop_ids);
    String stopCodesKey = getString(R.string.saved_stop_codes);
    String stopNamesKey = getString(R.string.saved_stop_names);

    savedStopIds.remove(position);
    savedStopCodes.remove(position);
    savedStopNames.remove(position);

    String stopIdsJson = gson.toJson(savedStopIds);
    String stopCodesJson = gson.toJson(savedStopCodes);
    String stopNamesJson = gson.toJson(savedStopNames);
    editor.putString(stopIdsKey, stopIdsJson);
    editor.putString(stopCodesKey, stopCodesJson);
    editor.putString(stopNamesKey, stopNamesJson);
    editor.apply();

    adapter.updateFavorites(position);
  }
}
