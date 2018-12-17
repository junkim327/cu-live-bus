package com.example.junyoung.culivebus.binding;

import androidx.databinding.DataBindingComponent;
import androidx.fragment.app.Fragment;

public class FragmentDataBindingComponent implements DataBindingComponent {
  private final FragmentBindingAdapters adapter;

  public FragmentDataBindingComponent(Fragment fragment) {
    this.adapter = new FragmentBindingAdapters(fragment);
  }
}
