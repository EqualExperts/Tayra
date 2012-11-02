package com.ee.beaver;

public class CreateCollectionPayload {
  private final String create;

  public CreateCollectionPayload(String name) {
    create = name;
  }
  public String capped;
  public String size;
  public String max;
}
