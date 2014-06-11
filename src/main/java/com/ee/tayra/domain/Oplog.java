/*******************************************************************************
 * Copyright (c) 2013, Equal Experts Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation
 * are those of the authors and should not be interpreted as representing
 * official policies, either expressed or implied, of the Tayra Project.
 ******************************************************************************/
package com.ee.tayra.domain;

import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONSerializers;

public class Oplog implements MongoCollection {

  private final DBCollection oplog;
  private static final String OPLOG_COLLECTIONNAME = "oplog.rs";
  private static final String OPLOG_DB_NAME = "local";

  public Oplog(final MongoClient mongo) {
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
    if (fromDocument == null || "".equals(fromDocument)) {
      return null;
    }

    DBObject timestamp = (DBObject) JSON.parse(fromDocument);
    return QueryBuilder
            .start()
              .put("ts")
              .greaterThan(timestamp.get("ts"))
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
      return JSONSerializers.getStrict().serialize(cursor.next());
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
