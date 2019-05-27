package com.example.junyoung.culivebus.httpclient.pojos;

import com.google.gson.annotations.Expose;

public class Status {
  @Expose
  private int code;
  @Expose
  private String msg;

  public Status(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
}
