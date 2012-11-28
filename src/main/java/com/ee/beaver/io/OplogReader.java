package com.ee.beaver.io;

import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.util.JSON;
import com.ee.beaver.domain.MongoCollection;
import com.ee.beaver.domain.MongoCollectionIterator;

public class OplogReader implements CollectionReader {

  private MongoCollectionIterator<DBObject> iterator;

  public OplogReader(final MongoCollection collection,
    final String fromDocument, final boolean tailable) {
    DBObject query = createQuery(fromDocument);
    iterator = collection.find(query, tailable);
  }

  private DBObject createQuery(final String fromDocument) {
    if (fromDocument == null) {
      return null;
    }

    DBObject timestamp = (DBObject) JSON.parse(fromDocument);
    return new QueryBuilder()
            .start()
              .put("ts")
              .greaterThan(timestamp.get("ts"))
            .get();
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
