package com.example.junyoung.culivebus.ui.settings;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.Button;

import androidx.databinding.BindingAdapter;

public class SettingsBindingAdapter {
  @BindingAdapter(value={"title", "subtitle"}, requireAll = false)
  public static void setButtonText(Button button, String title, String subtitle) {
    if (subtitle == null) {
      button.setText(title);
    } else {
      Spannable span = new SpannableString(title + "\n" + subtitle);
      span.setSpan(new RelativeSizeSpan(0.8f), title.length(),
        (title.length() + subtitle.length() + 1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      span.setSpan(new ForegroundColorSpan(Color.GRAY), title.length(),
        (title.length() + subtitle.length() + 1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      button.setText(span);
    }
  }
}
