package com.ee.beaver.runner;

public class NotAReplicaSetNode extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public NotAReplicaSetNode() {
    super();
  }

  public NotAReplicaSetNode(final String message) {
    super(message);
  }
}
