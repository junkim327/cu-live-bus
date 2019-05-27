package com.example.junyoung.culivebus.widget;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

public class CustomDimDialogFragment extends AppCompatDialogFragment {
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new CustomDimDialog(getContext());
  }
}
