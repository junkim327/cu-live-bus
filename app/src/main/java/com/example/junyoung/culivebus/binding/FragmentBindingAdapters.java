package com.example.junyoung.culivebus.binding;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.example.junyoung.culivebus.R;

import javax.inject.Inject;

import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;

public class FragmentBindingAdapters {
  final Fragment fragment;

  @Inject
  public FragmentBindingAdapters(Fragment fragment) {
    this.fragment = fragment;
  }

  @BindingAdapter("recentSearched")
  public static void loadSearchResultIcon(ImageView view, boolean recentSearched) {
    if (recentSearched) {
      view.setImageResource(R.drawable.ic_schedule_black_24dp);
    } else {
      view.setImageResource(R.drawable.ic_directions_bus_black_24dp);
    }
  }
}
