package com.example.junyoung.culivebus.di;

import com.example.junyoung.culivebus.ui.download.DownloadViewModel;
import com.example.junyoung.culivebus.ui.search.SearchViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.BusDepartureViewModel;
import com.example.junyoung.culivebus.ui.viewmodel.BusStopViewModel;
import com.example.junyoung.culivebus.viewmodel.CumtdViewModelFactory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
  @Binds
  @IntoMap
  @ViewModelKey(BusDepartureViewModel.class)
  abstract ViewModel bindBusDepartureViewModel(BusDepartureViewModel busDepartureViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(SearchViewModel.class)
  abstract ViewModel bindSearchViewModel(SearchViewModel searchViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(BusStopViewModel.class)
  abstract ViewModel bindBusStopViewModel(BusStopViewModel busStopViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(DownloadViewModel.class)
  abstract ViewModel bindDownloadViewModel(DownloadViewModel downloadViewModel);

  @Binds
  abstract ViewModelProvider.Factory bindViewModelFactory(CumtdViewModelFactory factory);
}
