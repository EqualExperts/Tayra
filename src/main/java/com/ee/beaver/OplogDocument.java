package com.ee.beaver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OplogDocument {
  public String ts;
  public String h;
  public String op;
  public String ns;
  public Object o;
  public String toJson() {
    Gson gson = new GsonBuilder().serializeNulls().create();
    return gson.toJson(this).replaceAll("\\\\", "");
  }
}
