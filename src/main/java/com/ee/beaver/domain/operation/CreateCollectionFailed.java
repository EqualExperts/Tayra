package com.ee.beaver.domain.operation;

public class CreateCollectionFailed extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CreateCollectionFailed() {
    super();
  }

  public CreateCollectionFailed(final String message) {
    super(message);
  }
}
