package com.example.junyoung.culivebus.ui.settings;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.binding.FragmentDataBindingComponent;
import com.example.junyoung.culivebus.databinding.ThirdPartySoftwareFragmentBinding;
import com.example.junyoung.culivebus.di.Injectable;
import com.example.junyoung.culivebus.ui.common.MainNavigationController;
import com.example.junyoung.culivebus.util.AutoClearedValue;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ThirdPartySoftwareFragment extends Fragment {
  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<ThirdPartySoftwareFragmentBinding> binding;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    ThirdPartySoftwareFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.fragment_third_party_software, container, false,
        dataBindingComponent);
    binding = new AutoClearedValue<>(this, dataBinding);

    initToolbar();

    return dataBinding.getRoot();
  }

  private void initToolbar() {
    //Toolbar toolbar = binding.get().toolbar;
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.getSupportActionBar().setTitle("Third Party Software");
    //activity.setSupportActionBar(toolbar);

//    toolbar.setNavigationOnClickListener(view -> {
//      FragmentManager fm = getFragmentManager();
//      if (fm != null && fm.getBackStackEntryCount() > 0) {
//        fm.popBackStack();
//      }
//    });
  }

  @BindingAdapter(value = {"softwareName", "author", "license"} , requireAll = true)
  public static void initLicenseButton(Button button, String softwareName,
                                       String author, String license) {
    Spannable span = new SpannableString(softwareName + "\n" + author);
    span.setSpan(new RelativeSizeSpan(0.8f), softwareName.length(),
      (softwareName.length() + author.length() + 1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    span.setSpan(new ForegroundColorSpan(Color.GRAY), softwareName.length(),
      (softwareName.length() + author.length() + 1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    button.setText(span);

    Context context = button.getContext();
    button.setOnClickListener(view -> {
      LicenseDialogFragment fragment = LicenseDialogFragment.newInstance(softwareName, license);
      fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "dialog");
    });
  }
}
