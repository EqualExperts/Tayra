package com.ee.tayra.domain;

import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryBuilder;
import com.mongodb.util.JSON;

public class Oplog implements MongoCollection {

  private final DBCollection oplog;
  private static final String OPLOG_COLLECTIONNAME = "oplog.rs";
  private static final String OPLOG_DB_NAME = "local";

  public Oplog(final Mongo mongo) {
    DB local = mongo.getDB(OPLOG_DB_NAME);

    boolean oplogExists = local.collectionExists(OPLOG_COLLECTIONNAME);
    if (!oplogExists) {
      throw new NotAReplicaSetNode("node is not a part of ReplicaSet");
    }

    oplog = local.getCollection(OPLOG_COLLECTIONNAME);
  }

  @Override
  public final MongoCollectionIterator<String> find(final String
    fromDocument, final boolean tailable) {
    DBObject query = createQuery(fromDocument);
    return new OplogIterator(oplog, query, tailable);
  }

  private DBObject createQuery(final String fromDocument) {
    if (fromDocument == null) {
      return null;
    }

    DBObject timestamp = (DBObject) JSON.parse(fromDocument);
    return QueryBuilder
            .start()
              .put("ts")
              .greaterThanEquals(timestamp.get("ts"))
            .get();
    }

  private static class OplogIterator implements
    MongoCollectionIterator<String> {

    private DBCursor cursor;

    public OplogIterator(final DBCollection collection, final DBObject
    query, final boolean tailable) {
      cursor = collection.find(query);
      if (tailable) {
          cursor.addOption(Bytes.QUERYOPTION_TAILABLE);
          cursor.addOption(Bytes.QUERYOPTION_AWAITDATA);
        }
    }

    @Override
    public boolean hasNext() {
      if (cursor == null) {
        throw new IteratorAlreadyClosed("Iterator Already Closed");
      }
      return cursor.hasNext();
    }

    @Override
    public String next() {
      if (cursor == null) {
            throw new IteratorAlreadyClosed("Iterator Already Closed");
      }
      return JSON.serialize(cursor.next());
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException(
        "remove document on oplog is not supported"
      );
    }

    @Override
    public void close() {
      cursor.close();
      cursor = null;
    }
  }

}
