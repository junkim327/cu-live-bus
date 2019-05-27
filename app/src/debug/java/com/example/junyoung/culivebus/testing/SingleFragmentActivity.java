package com.example.junyoung.culivebus.testing;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.junyoung.culivebus.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class SingleFragmentActivity extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FrameLayout content = new FrameLayout(this);
    content.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT));
    content.setId(R.id.fragment_container);
    setContentView(content);
  }

  public void setFragment(Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
      .add(R.id.fragment_container, fragment, "TEST")
      .commit();
  }

  public void replaceFragment(Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
      .replace(R.id.fragment_container, fragment).commit();
  }
}
