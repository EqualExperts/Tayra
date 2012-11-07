package com.ee.beaver.domain;

public class NotAReplicaSetNode extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public NotAReplicaSetNode(final String message) {
    super(message);
  }
}
