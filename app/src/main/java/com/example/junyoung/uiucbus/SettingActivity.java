package com.example.junyoung.uiucbus;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.junyoung.uiucbus.fragments.EditFavoriteFragment;
import com.example.junyoung.uiucbus.fragments.SettingContentsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity
  implements SettingContentsFragment.OnEditFavoritesClickListener{
  private static final String TAG = SettingActivity.class.getSimpleName();
  private EditFavoriteFragment editFavoriteFragment;
  private SettingContentsFragment settingContentsFragment;

  @BindView(R.id.toolbar_setting)
  Toolbar toolbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    ButterKnife.bind(this);
    if (savedInstanceState == null) {
      settingContentsFragment = new SettingContentsFragment();
    }

    setSupportActionBar(toolbar);
    toolbar.setTitleTextColor(Color.WHITE);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(getResources().getString(R.string.setting_toolbar_title));
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    if (findViewById(R.id.framelayout_setting) != null) {
      getSupportFragmentManager().beginTransaction()
        .add(R.id.framelayout_setting, settingContentsFragment)
        .commit();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onEditFavoritesClick() {

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(getString(R.string.setting_edit_favorites));
    }

    editFavoriteFragment = new EditFavoriteFragment();
    getSupportFragmentManager().beginTransaction()
      .replace(R.id.framelayout_setting, editFavoriteFragment)
      .addToBackStack(null)
      .commit();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(getString(R.string.setting_toolbar_title));
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume has started");
  }
}
