package com.example.junyoung.uiucbus.httpclient.pojos;

import com.google.gson.annotations.Expose;

public class Status {
  @Expose
  private int code;
  @Expose
  private String msg;

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
}
