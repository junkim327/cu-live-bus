package com.example.junyoung.uiucbus;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
  @BindView(R.id.toolbar_main) Toolbar mainToolbar;
  @BindView(R.id.viewpager_main) ViewPager mainViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    setSupportActionBar(mainToolbar);

    mainViewPager.setClipToPadding(false);
    mainViewPager.setPageMargin(24);
    mainViewPager.setAdapter(new MainViewPagerAdapter(this));
  }
}
