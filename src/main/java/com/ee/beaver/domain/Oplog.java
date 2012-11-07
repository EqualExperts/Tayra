package com.ee.beaver.domain;

import java.util.Iterator;

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
  public final Iterator<DBObject> find() {
    return new OplogIterator(oplog);
  }

  private static class OplogIterator implements Iterator<DBObject> {

    private final DBCursor cursor;

    public OplogIterator(final DBCollection oplog) {
      cursor = oplog.find();
    }

    @Override
    public boolean hasNext() {
      return cursor.hasNext();
    }

    @Override
    public DBObject next() {
      return cursor.next();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException(
        "remove document on oplog is not supported"
      );
    }
  }
}
