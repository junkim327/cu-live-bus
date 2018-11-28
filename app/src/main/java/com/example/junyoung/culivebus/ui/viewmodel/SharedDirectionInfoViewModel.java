package com.example.junyoung.culivebus.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.junyoung.culivebus.room.entity.RouteInfo;

public class SharedDirectionInfoViewModel extends ViewModel {
 private final MutableLiveData<RouteInfo> mRouteInfo = new MutableLiveData<>();

 public void select(RouteInfo routeInfo) {
   mRouteInfo.setValue(routeInfo);
 }

 public LiveData<RouteInfo> getRouteInfo() {
   return mRouteInfo;
 }
}
