package com.ee.beaver;

import org.bson.types.BSONTimestamp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OplogDocument {
  public BSONTimestamp ts;
  public long h;
  public String op;
  public String ns;
  public Object o;

  public final String toJson() {
    Gson gson = new GsonBuilder().serializeNulls().create();
    return gson.toJson(this).replaceAll("\\\\", "");
  }
}
