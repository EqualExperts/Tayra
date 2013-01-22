package com.ee.tayra.domain.operation;

public class RenameCollectionFailed extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public RenameCollectionFailed(final String message) {
    super(message);
  }
}
