package com.ee.beaver.io;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.ee.beaver.domain.MongoCollection;
import com.ee.beaver.domain.MongoCollectionIterator;
import com.ee.beaver.domain.ReaderAlreadyClosed;

public class OplogReader implements CollectionReader {

  private MongoCollectionIterator<DBObject> iterator;

  public OplogReader(final MongoCollection collection, final boolean
    tailable) {
    iterator = collection.find(tailable);
  }

  @Override
  public final boolean hasDocument() {
    if (iterator == null) {
        throw new ReaderAlreadyClosed("Reader Already Closed");
    }
    return iterator.hasNext();
  }

  @Override
  public final String readDocument() {
    if (iterator == null) {
      throw new ReaderAlreadyClosed("Reader Already Closed");
    }
    DBObject dbObject = iterator.next();
    return JSON.serialize(dbObject);
  }

  public final void close() {
    iterator.close();
    iterator = null;
  }

}
