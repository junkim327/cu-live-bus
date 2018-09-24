package com.example.junyoung.uiucbus.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.junyoung.uiucbus.R;
import com.example.junyoung.uiucbus.RecyclerviewClickListener;
import com.example.junyoung.uiucbus.SharedPrefUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

public class EditFavoritesAdapter extends Adapter<EditFavoritesAdapter.EditFavoriteViewHolder>
  implements ItemLongClickHelperAdapter {
  public static class EditFavoriteViewHolder extends ViewHolder implements View.OnClickListener {
    public TextView busStopNameTextView;
    public TextView busStopCodeTextView;
    public ImageButton favoriteImageButton;

    private RecyclerviewClickListener listener;

    public EditFavoriteViewHolder(View itemView, RecyclerviewClickListener listener) {
      super(itemView);

      this.listener = listener;
      busStopNameTextView = itemView.findViewById(R.id.text_bus_stop_name_card_edit_favorites);
      busStopCodeTextView = itemView.findViewById(R.id.text_bus_stop_code_card_edit_favorites);
      favoriteImageButton = itemView.findViewById(R.id.image_button_favorite_card_edit_favorites);

      favoriteImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      int pos = getAdapterPosition();
      if (pos != RecyclerView.NO_POSITION) {
        listener.onClick(view, pos);
      }
    }
  }

  private ArrayList<String> savedStopIds;
  private ArrayList<String> savedStopCodes;
  private ArrayList<String> savedStopNames;

  private Context context;
  private RecyclerviewClickListener listener;

  public EditFavoritesAdapter(Context context, RecyclerviewClickListener listener) {
    this.context = context;
    this.listener = listener;

    SharedPreferences sharedPref = context.getSharedPreferences(
      context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    savedStopIds =
      SharedPrefUtil.getArrayList(sharedPref, context.getString(R.string.saved_stop_ids));
    savedStopCodes =
      SharedPrefUtil.getArrayList(sharedPref, context.getString(R.string.saved_stop_codes));
    savedStopNames =
      SharedPrefUtil.getArrayList(sharedPref, context.getString(R.string.saved_stop_names));
  }

  public void updateFavorites(int position) {
    SharedPreferences sharedPref = context.getSharedPreferences(
      context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    savedStopIds =
      SharedPrefUtil.getArrayList(sharedPref, context.getString(R.string.saved_stop_ids));
    savedStopCodes =
      SharedPrefUtil.getArrayList(sharedPref, context.getString(R.string.saved_stop_codes));
    savedStopNames =
      SharedPrefUtil.getArrayList(sharedPref, context.getString(R.string.saved_stop_names));

    notifyItemRemoved(position);
    notifyItemRangeChanged(position, getItemCount());
  }

  @NonNull
  @Override
  public EditFavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View editFavoriteView = inflater.inflate(R.layout.card_edit_favorites, parent, false);
    EditFavoriteViewHolder vh = new EditFavoriteViewHolder(editFavoriteView, listener);

    return vh;
  }

  @Override
  public void onBindViewHolder(@NonNull EditFavoriteViewHolder holder, int position) {
    holder.busStopNameTextView.setText(savedStopNames.get(position));
    holder.busStopCodeTextView.setText(savedStopCodes.get(position));
  }

  @Override
  public int getItemCount() {
    return savedStopCodes.size();
  }

  @Override
  public boolean onItemMove(int fromPosition, int toPosition) {
    notifyItemMoved(fromPosition, toPosition);
    Collections.swap(savedStopIds, fromPosition, toPosition);
    Collections.swap(savedStopCodes, fromPosition, toPosition);
    Collections.swap(savedStopNames, fromPosition, toPosition);

    SharedPreferences sharedPref = context.getSharedPreferences(
      context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    );
    SharedPreferences.Editor editor = sharedPref.edit();
    Gson gson = new Gson();

    String stopIdsJson = gson.toJson(savedStopIds);
    String stopCodesJson = gson.toJson(savedStopCodes);
    String stopNamesJson = gson.toJson(savedStopNames);
    editor.putString(context.getString(R.string.saved_stop_ids), stopIdsJson);
    editor.putString(context.getString(R.string.saved_stop_codes), stopCodesJson);
    editor.putString(context.getString(R.string.saved_stop_names), stopNamesJson);
    editor.apply();

    return true;
  }
}
