package com.ee.beaver.io;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.ee.beaver.domain.MongoCollection;
import com.ee.beaver.domain.MongoCollectionIterator;

public class OplogReader implements CollectionReader {

  private final MongoCollectionIterator<DBObject> iterator;

  public OplogReader(final MongoCollection collection, final boolean
    readContinuously) {
      iterator = collection.find(readContinuously);
  }

  @Override
  public final boolean hasDocument() {
    return iterator.hasNext();
  }

  @Override
  public final String readDocument() {
    DBObject dbObject = iterator.next();
    return JSON.serialize(dbObject);
  }

  public void close() {
    iterator.close();
  }

}
