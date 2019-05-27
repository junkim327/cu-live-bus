package com.example.junyoung.culivebus.util;

import com.example.junyoung.culivebus.vo.Function;

import androidx.lifecycle.Observer;
import timber.log.Timber;

public class EventObserver<T> implements Observer<Event<T>> {
  private Function<T> onEventUnhandledContent;

  public EventObserver(Function<T> eventUnhandledContent) {
    super();
    this.onEventUnhandledContent = eventUnhandledContent;
  }

  @Override
  public void onChanged(Event<T> event) {
    if (event != null) {
      Timber.d("Event Not Null");
      T content = event.getContentIfNotHandled();
      if (content != null) {
        Timber.d("Content Not Null");
        this.onEventUnhandledContent.invoke(content);
      }
    }
  }
}
