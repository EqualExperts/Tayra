package com.ee.beaver.domain.operation;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBObject;

class CreateCollection implements SchemaOperation {

  private static final String MAX = "max";
  private static final String SIZE = "size";
  private static final String CAPPED = "capped";

  public final void doExecute(final DB db, final DBObject spec) {
    final String collectionName = (String) spec.get("create");
    DBObject options = BasicDBObjectBuilder
                       .start()
                         .add(CAPPED, spec.get(CAPPED))
                         .add(SIZE, spec.get(SIZE))
                         .add(MAX, spec.get(MAX))
                       .get();
    try {
      db.createCollection(collectionName, options);
    } catch (Exception problem) {
      throw new CreateCollectionFailed(problem.getMessage());
    }
  }

}
