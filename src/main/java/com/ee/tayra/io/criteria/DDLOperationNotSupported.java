package com.ee.tayra.io.criteria;

public class DDLOperationNotSupported extends RuntimeException {
  private static final long serialVersionUID = 1L;

    public DDLOperationNotSupported(final String message) {
      super(message);
    }
}
