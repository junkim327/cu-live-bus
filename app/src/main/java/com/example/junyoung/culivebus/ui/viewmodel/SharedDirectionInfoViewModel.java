package com.example.junyoung.culivebus.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.junyoung.culivebus.db.entity.RouteInfo;

public class SharedDirectionInfoViewModel extends ViewModel {
 private final MutableLiveData<RouteInfo> mRouteInfo = new MutableLiveData<>();

 public void select(RouteInfo routeInfo) {
   mRouteInfo.setValue(routeInfo);
 }

 public LiveData<RouteInfo> getRouteInfo() {
   return mRouteInfo;
 }
}
