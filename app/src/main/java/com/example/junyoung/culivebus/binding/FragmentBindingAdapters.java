package com.example.junyoung.culivebus.binding;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.util.TimeFormatter;

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

  @BindingAdapter(value = {"startTime", "endTime"}, requireAll = true)
  public static void getTimeInterval(TextView textView, String startTime, String endTime) {
    textView.setText(TimeFormatter.getTimeInterval(textView.getContext(), startTime, endTime,
      "24hr"));
  }
}
