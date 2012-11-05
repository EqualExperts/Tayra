package com.ee.beaver;

import java.util.Iterator;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class OplogReader {

  private final Iterator<DBObject> iterator;

  public OplogReader(final MongoCollection collection) {
    iterator = collection.find();
  }

  public final boolean hasDocument() {
    return iterator.hasNext();
  }

  public final String readDocument() {
    DBObject dbObject = iterator.next();
    return JSON.serialize(dbObject);
  }
}
