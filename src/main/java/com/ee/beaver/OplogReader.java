package com.ee.beaver;

import java.util.Iterator;

public class OplogReader {

  private final OplogCollection collection;

  public OplogReader(final OplogCollection aCollection) {
    this.collection = aCollection;
  }

  public final OplogDocument readDocument() {
    Iterator<OplogDocument> iterator = collection.iterator();
      while (iterator.hasNext()) {
        OplogDocument oplogDocument = iterator.next();
          return oplogDocument;
      }
  return null;
  }
}
