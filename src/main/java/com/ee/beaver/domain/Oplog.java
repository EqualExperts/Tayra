package com.ee.beaver.domain;

import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Oplog implements MongoCollection {

  private final DBCollection oplog;
  private static final String OPLOG_COLLECTIONNAME = "oplog.rs";

  public Oplog(final DB local) {
    if (!"local".equals(local.getName())) {
      throw new NotALocalDB("Not a local DB");
    }

    boolean oplogExists = local.collectionExists(OPLOG_COLLECTIONNAME);
    if (!oplogExists) {
      throw new NotAReplicaSetNode("localhost is not a part of ReplicaSet");
    }

    oplog = local.getCollection(OPLOG_COLLECTIONNAME);
  }

  @Override
  public final MongoCollectionIterator<DBObject> find() {
    return find(false);
  }

  @Override
  public final MongoCollectionIterator<DBObject> find(final boolean tailable) {
    return new OplogIterator(oplog, tailable);
  }

  private static class OplogIterator implements
    MongoCollectionIterator<DBObject> {

    private DBCursor cursor;

    public OplogIterator(final DBCollection collection,
        final boolean tailable) {
      cursor = collection.find();
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
    public DBObject next() {
      if (cursor == null) {
            throw new IteratorAlreadyClosed("Iterator Already Closed");
      }
      return cursor.next();
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
