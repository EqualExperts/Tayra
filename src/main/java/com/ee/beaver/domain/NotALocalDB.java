package com.ee.beaver.domain;

public class NotALocalDB extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NotALocalDB(final String message) {
    super(message);
  }
}
