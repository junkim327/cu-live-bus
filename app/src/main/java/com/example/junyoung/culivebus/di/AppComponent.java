package com.example.junyoung.culivebus.di;

import com.example.junyoung.culivebus.CuLiveBusApp;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
  AndroidSupportInjectionModule.class,
  AppModule.class,
  ActivityBindingModule.class,
  ServiceBindingModule.class,
  ViewModelModule.class
})
interface AppComponent extends AndroidInjector<CuLiveBusApp> {
  @Component.Builder
  abstract class Builder extends AndroidInjector.Builder<CuLiveBusApp> {}
}
