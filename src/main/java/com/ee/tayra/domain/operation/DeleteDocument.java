package com.ee.tayra.domain.operation;

import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

class DeleteDocument extends Operation {

  private final Mongo mongo;

  public DeleteDocument(final Mongo mongo) {
    this.mongo = mongo;
  }

  @Override
  protected final void doExecute(final DBObject document) {
    final String ns = (String) document.get("ns");
    int index = ns.indexOf(".");
    if (index != -1) {
      String dbName = ns.substring(0, index);
      String collectionName = ns.substring(index + 1, ns.length());
      DBObject deleteSpec = (DBObject) JSON.parse(document.get("o").toString());
      WriteResult writeResult = mongo.getDB(dbName)
                                     .getCollection(collectionName)
                                     .remove(deleteSpec);
      int deletedObjCount = writeResult.getN();
      if (deletedObjCount == 0) {
        throw new DeleteFailed("Document does not exist "
              + deleteSpec.toString());
      }
    }
  }
}
