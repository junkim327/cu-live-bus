package com.example.junyoung.culivebus.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.binding.FragmentDataBindingComponent;
import com.example.junyoung.culivebus.databinding.LicenseDialogFragmentBinding;
import com.example.junyoung.culivebus.di.Injectable;
import com.example.junyoung.culivebus.util.AutoClearedValue;
import com.example.junyoung.culivebus.widget.CustomDimDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;

public class LicenseDialogFragment extends CustomDimDialogFragment implements Injectable {
  private final static String SOFTWARE_NAME_ID = "SOFTWARE_NAME_ID";
  private final static String LICENSE_ID = "LICENSE_ID";

  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<LicenseDialogFragmentBinding> binding;


  static LicenseDialogFragment newInstance(String softwareName, String license) {
    LicenseDialogFragment fragment = new LicenseDialogFragment();

    Bundle args = new Bundle();
    args.putString(SOFTWARE_NAME_ID, softwareName);
    args.putString(LICENSE_ID, license);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    LicenseDialogFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.dialog_license, container, false, dataBindingComponent);
    dataBinding.okButton.setOnClickListener(view -> {
      dismiss();
    });
    binding = new AutoClearedValue<>(this, dataBinding);

    return dataBinding.getRoot();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Bundle args = getArguments();
    if (args != null && args.containsKey(SOFTWARE_NAME_ID) && args.containsKey(LICENSE_ID)) {
      binding.get().setSoftwareName(args.getString(SOFTWARE_NAME_ID) + " License");
      binding.get().setLicense(args.getString(LICENSE_ID));
      binding.get().executePendingBindings();
    }
  }
}
