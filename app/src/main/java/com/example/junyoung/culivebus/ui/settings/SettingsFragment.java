package com.example.junyoung.culivebus.ui.settings;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.junyoung.culivebus.R;
import com.example.junyoung.culivebus.binding.FragmentDataBindingComponent;
import com.example.junyoung.culivebus.databinding.SettingsFragmentBinding;
import com.example.junyoung.culivebus.di.Injectable;
import com.example.junyoung.culivebus.ui.common.MainNavigationController;
import com.example.junyoung.culivebus.util.AutoClearedValue;

import javax.inject.Inject;

import androidx.fragment.app.FragmentManager;

public class SettingsFragment extends Fragment implements Injectable {
  @Inject
  MainNavigationController mainNavigationController;

  DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
  AutoClearedValue<SettingsFragmentBinding> binding;
  // private OnEditFavoritesClickListener onEditFavoritesCallback;

  /*
  public interface OnEditFavoritesClickListener {
    void onEditFavoritesClick();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    try {
      onEditFavoritesCallback = (OnEditFavoritesClickListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
      + " must implement OnEditFavoritesClickListener");
    }
  }
  */

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    SettingsFragmentBinding dataBinding = DataBindingUtil
      .inflate(inflater, R.layout.fragment_setting, container, false, dataBindingComponent);
    binding = new AutoClearedValue<>(this, dataBinding);

    binding.get().buttonThirdPartySoftware.setOnClickListener(view ->
      mainNavigationController.navigateToThirdPartySoftware()
    );

    initToolbar();

    return dataBinding.getRoot();
  }

  private void initToolbar() {
    Toolbar toolbar = binding.get().toolbar;
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);

    toolbar.setNavigationOnClickListener(view -> {
      FragmentManager fm = getFragmentManager();
      if (fm != null && fm.getBackStackEntryCount() > 0) {
        fm.popBackStack();
      }
    });
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  /*
  @OnClick(R.id.layout_contact_us_settting_content)
  public void composeEmail() {
    Intent intent = new Intent(Intent.ACTION_SENDTO);
    intent.setData(Uri.parse("mailto:"));
    String[] arr = new String[1];
    arr[0] = getResources().getString(R.string.email_address);
    intent.putExtra(Intent.EXTRA_EMAIL, arr);
    intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.subject));
    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
      startActivity(intent);
    }
  }*/

  /*
  @OnClick(R.id.layout_edit_favorites_setting_content)
  public void editFavorites() {
    onEditFavoritesCallback.onEditFavoritesClick();
  }
  */
}
