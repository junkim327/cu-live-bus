package com.example.junyoung.culivebus.ui.download;

import android.os.Bundle;

import com.example.junyoung.culivebus.R;

import androidx.annotation.Nullable;
import dagger.android.support.DaggerAppCompatActivity;

public class DownloadActivity extends DaggerAppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_download);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
        .add(R.id.framelayout_download, new DownloadFragment())
        .commit();
    }
  }
}
