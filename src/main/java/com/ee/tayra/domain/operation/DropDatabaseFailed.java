package com.ee.tayra.domain.operation;

public class DropDatabaseFailed extends RuntimeException {

private static final long serialVersionUID = 1L;

  public DropDatabaseFailed(final String message) {
    super(message);
  }
}
