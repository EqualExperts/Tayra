package com.ee.tayra.domain.operation;

import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

class InsertDocument extends Operation {

  private static final String INDEX_NAMESPACE = "system.indexes";
  private final Mongo mongo;
  private String dbName;
  private String collectionName;

  public InsertDocument(final Mongo mongo) {
    this.mongo = mongo;
  }

  @Override
  protected final void doExecute(final DBObject document) {
    final String ns = (String) document.get("ns");
    int index = ns.indexOf(".");
    DBObject spec = (DBObject) JSON.parse(document.get("o").toString());

    if (index != -1) {
      if (ns.contains(INDEX_NAMESPACE)) {
      String indexNamespace = spec.get("ns").toString();
      extractParametersFrom(indexNamespace);
      DBObject key = (DBObject) spec.get("key");
      try {
        mongo.getDB(dbName).getCollection(collectionName)
                           .ensureIndex(key, spec);
      } catch (Exception problem) {
        throw new InsertFailed(problem.getMessage());
      }
    } else {
      extractParametersFrom(ns);
      try {
        mongo.getDB(dbName).getCollection(collectionName).insert(spec);
      } catch (Exception problem) {
        throw new InsertFailed(problem.getMessage());
      }
    }
  }
}

  private void extractParametersFrom(final String namespace) {
    int index = namespace.indexOf(".");
    dbName = namespace.substring(0, index);
    collectionName = namespace.substring(index + 1, namespace.length());
  }
}
