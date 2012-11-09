package com.ee.beaver.domain.operation;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

public class CreateCollection implements Operation {

  private final Mongo mongo;

  public CreateCollection(final Mongo mongo) {
    this.mongo = mongo;
  }

  @Override
  public final void execute(final DBObject document) {
    final String ns = (String) document.get("ns");
    String[] dbInfo = ns.split("\\.");
    String dbName = dbInfo[0];
    String dbCommand = dbInfo[1];
    if ("$cmd".equals(dbCommand)) {
      DB db = mongo.getDB(dbName);
      DBObject createSpec = (DBObject) JSON.parse(document.get("o").toString());
      final String collectionName = (String) createSpec.get("create");
      DBObject options = new BasicDBObjectBuilder()
                             .start()
                             .add("capped", createSpec.get("capped"))
                             .add("size", createSpec.get("size"))
                             .add("max", createSpec.get("max"))
                             .get();
      try {
        db.createCollection(collectionName, options);
      } catch (Exception problem) {
        throw new CreateCollectionFailed(problem.getMessage());
      }
    }
  }
}
