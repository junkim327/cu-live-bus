package com.example.junyoung.uiucbus.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.junyoung.uiucbus.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingContentsFragment extends Fragment {
  private Unbinder unbinder;
  private OnEditFavoritesClickListener onEditFavoritesCallback;

  @BindView(R.id.layout_contact_us_settting_content)
  LinearLayout contactUsLayout;
  @BindView(R.id.layout_edit_favorites_setting_content)
  LinearLayout editFavoritesLayout;
  @BindView(R.id.layout_privacy_policy_setting_content)
  LinearLayout privacyPolicyLayout;
  @BindView(R.id.layout_data_attribution_setting_content)
  LinearLayout dataAttributionLayout;

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

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_setting_contents, container, false);
    unbinder = ButterKnife.bind(this, view);

    return view;
  }

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
  }

  @OnClick(R.id.layout_edit_favorites_setting_content)
  public void editFavorites() {
    onEditFavoritesCallback.onEditFavoritesClick();
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
