package com.example.junyoung.culivebus.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junyoung.culivebus.BusStopActivity;
import com.example.junyoung.culivebus.GetDeparturesActivity;
import com.example.junyoung.culivebus.R;

public class MainViewPagerAdapter extends PagerAdapter {
  private String[] headerList;

  private Context context;

  public MainViewPagerAdapter(Context context) {
    this.context = context;
    this.headerList = new String[]{"Nearby stops", "Get Directions"};
  }

  @Override
  public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    container.removeView((View) object);
  }

  @Override
  public int getCount() {
    return 2;
  }

  @Override
  public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
    return view == object;
  }

  @NonNull
  @Override
  public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
    LayoutInflater inflater = LayoutInflater.from(context);
    ViewGroup view = (ViewGroup) inflater.inflate(R.layout.main_view_item, container, false);

    final TextView header = view.findViewById(R.id.text_header_main_view_item);
    header.setText(headerList[position]);
    if (position == 1) {
      ImageView icon = view.findViewById(R.id.image_icon_main_view_item);
      icon.setImageDrawable(context.getDrawable(R.drawable.ic_route_on_phone_color_resize));
    }

    view.setOnClickListener(view1 -> {
      if (position == 0) {
        Intent intent = new Intent(context, BusStopActivity.class);
        context.startActivity(intent);
      } else if (position == 1) {
        Intent intent = new Intent(context, GetDeparturesActivity.class);
        context.startActivity(intent);
      }
    });

    container.addView(view);

    return view;
  }
}
