package com.ee.tayra.domain.operation;

import com.mongodb.MongoException;

public class OperationFailed extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final Throwable problem;

  public OperationFailed(final String message, final Throwable cause) {
    super(message);
    problem = cause;
  }

  public final boolean isConnectionLost() {
    if (problem.getClass() == MongoException.Network.class) {
      return true;
    }
    return false;
  }

  public OperationFailed(final String message) {
    super(message);
    problem = new RuntimeException();
  }
}
