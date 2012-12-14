package com.ee.beaver.domain.operation;

public class DropIndexFailed extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public DropIndexFailed(final String message) {
    super(message);
  }
}
