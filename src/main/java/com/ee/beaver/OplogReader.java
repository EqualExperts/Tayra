package com.ee.beaver;

import java.util.Iterator;

public class OplogReader {

  private final Iterator<OplogDocument> iterator;

  public OplogReader(final MongoCollection collection) {
    iterator = collection.find();
  }

  public final boolean hasDocument() {
    return iterator.hasNext();
  }

  public final OplogDocument readDocument() {
    return iterator.next();
  }
}
