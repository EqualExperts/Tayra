package com.ee.beaver.domain.operation;

class CreateCollectionFailed extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CreateCollectionFailed(final String message) {
    super(message);
  }
}
