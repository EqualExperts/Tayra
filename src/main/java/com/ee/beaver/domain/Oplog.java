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
  public final MongoCollectionIterator<DBObject> find (boolean tailable) {
    return new OplogIterator(oplog, tailable);
  }

  private static class OplogIterator implements MongoCollectionIterator<DBObject> {

    private final DBCursor cursor;
    private final boolean tailable;

    public OplogIterator (final DBCollection collection, final boolean tailable) {
      this.tailable = tailable;
      cursor = collection.find();
      if (tailable) {
        cursor.addOption(Bytes.QUERYOPTION_TAILABLE);
        cursor.addOption(Bytes.QUERYOPTION_AWAITDATA);
      }
    }

    @Override
    public boolean hasNext() {
  	  if(tailable) {
      while(!cursor.hasNext() && noExplicitBreak());
        return true;
      } else {
      return cursor.hasNext();
      }
    }

    private boolean noExplicitBreak() {
      if(cursor.count() > 125) {
      close();
      return false;
      }
      else {
       return true;
      }
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

    @Override
    public void close() {
      cursor.close();
    }
  }
}
