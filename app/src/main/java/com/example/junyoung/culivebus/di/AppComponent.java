package com.example.junyoung.culivebus.di;

import android.app.Application;

import com.example.junyoung.culivebus.CuLiveBusApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
  AndroidInjectionModule.class,
  MainActivityModule.class
})
public interface AppComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance Builder application(Application application);
    AppComponent build();
  }
  void inject(CuLiveBusApp cuLiveBusApp);
}
