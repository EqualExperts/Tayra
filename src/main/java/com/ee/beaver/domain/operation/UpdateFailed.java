package com.ee.beaver.domain.operation;

public class UpdateFailed extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public UpdateFailed(final String message) {
    super(message);
  }
}
