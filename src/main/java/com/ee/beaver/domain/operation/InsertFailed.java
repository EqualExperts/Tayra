package com.ee.beaver.domain.operation;

public class InsertFailed extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InsertFailed() {
    super();
  }

  public InsertFailed(final String message) {
    super(message);
  }
}
