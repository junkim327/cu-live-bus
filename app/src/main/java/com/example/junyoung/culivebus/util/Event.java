package com.example.junyoung.culivebus.util;

public class Event<T> {
  private boolean hasBeenHandled;
  private final T content;

  public Event(T content) {
    this.content = content;
  }

  public T getContentIfNotHandled() {
    T content;
    if (hasBeenHandled) {
      content = null;
    } else {
      hasBeenHandled = true;
      content = this.content;
    }

    return content;
  }

  public T peekContent() {
    return this.content;
  }

  public boolean getHasBeenHandled() {
    return this.hasBeenHandled;
  }

  public void setHasBeenHandled(boolean hasBeenHandled) {
    this.hasBeenHandled = hasBeenHandled;
  }
}
