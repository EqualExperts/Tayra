package com.ee.beaver.domain.operation;

import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

class InsertDocument extends Operation {

  private final Mongo mongo;

  public InsertDocument(final Mongo mongo) {
    this.mongo = mongo;
  }
  @Override
  protected final void doExecute(final DBObject document) {
    final String ns = (String) document.get("ns");
    int index = ns.indexOf(".");
    if (index != -1) {
      String dbName = ns.substring(0, index);
      String collectionName = ns.substring(index + 1, ns.length());
      DBObject insertSpec = (DBObject) JSON.parse(document.get("o").toString());
      try {
          mongo.getDB(dbName).getCollection(collectionName).insert(insertSpec);
        } catch (Exception problem) {
          throw new InsertFailed(problem.getMessage());
        }
    }
  }
}
