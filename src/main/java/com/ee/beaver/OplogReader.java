package com.ee.beaver;

import java.util.Iterator;

public class OplogReader {

  private final MongoCollection collection;

  public OplogReader(final MongoCollection aCollection) {
    this.collection = aCollection;
  }

  public final OplogDocument readDocument() {
    Iterator<OplogDocument> iterator = collection.find();
      while (iterator.hasNext()) {
        OplogDocument oplogDocument = iterator.next();
        return oplogDocument;
      }
    return null;
  }
}
