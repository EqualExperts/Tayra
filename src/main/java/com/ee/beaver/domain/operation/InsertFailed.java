package com.ee.beaver.domain.operation;

class InsertFailed extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InsertFailed(final String message) {
    super(message);
  }
}
