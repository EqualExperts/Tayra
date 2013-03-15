package com.ee.tayra.io;

import com.ee.tayra.io.criteria.Criterion;

public class SelectiveOplogReader implements CollectionReader {

  private final CollectionReader delegate;
  private final Criterion criteria;

  public SelectiveOplogReader(final CollectionReader delegate,
      final Criterion criteria) {
    this.delegate = delegate;
    this.criteria = criteria;
  }

  @Override
  public final String readDocument() {
    String document = delegate.readDocument();
    if (criteria.isSatisfiedBy(document)) {
      return document;
    }
    return new String("");
  }

  @Override
  public final boolean hasDocument() {
    return delegate.hasDocument();
  }

  @Override
  public final void close() {
    delegate.close();
  }
}
