package com.ee.beaver.domain.operation;

import com.mongodb.DB;
import com.mongodb.DBObject;

public class DropCollection implements SchemaOperation {

  public final void execute(final DB db, final DBObject spec) {
    final String dropCollectionName = (String) spec.get("drop");
    if (!db.collectionExists(dropCollectionName)) {
      throw new DropCollectionFailed("Could Not Drop Collection "
          + dropCollectionName);
    }
    db.getCollection(dropCollectionName).drop();
}

}
