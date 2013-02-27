package com.ee.tayra.io;

import com.ee.tayra.io.criteria.Criterion;

public class SelectiveOplogReader implements CollectionReader {

  private final CollectionReader oplogReader;
  private final Criterion criteria;

  public SelectiveOplogReader(final CollectionReader oplogReader,
      final Criterion criteria) {
    this.oplogReader = oplogReader;
    this.criteria = criteria;
  }

  @Override
  public final String readDocument() {
    String document = oplogReader.readDocument();
    if (criteria.isSatisfiedBy(document)) {
      return document;
    }
    return null;
  }

  @Override
  public final boolean hasDocument() {
    return oplogReader.hasDocument();
  }

  @Override
  public final void close() {
    oplogReader.close();
  }
}
