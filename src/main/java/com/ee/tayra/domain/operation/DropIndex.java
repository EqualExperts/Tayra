package com.ee.tayra.domain.operation;

import com.mongodb.DB;
import com.mongodb.DBObject;

public class DropIndex implements SchemaOperation {

  @Override
  public final void doExecute(final DB db, final DBObject spec) {
    String collectionName = (String) spec.get("deleteIndexes");
    if (shouldDropAllIndexes(spec)) {
      db.getCollection(collectionName).dropIndexes();
    } else {
      dropSingleIndex(db, spec, collectionName);
    }
  }

  private void dropSingleIndex(final DB db, final DBObject spec,
      final String collectionName) {
    DropIndexStrategy strategy = DropIndexStrategy.ABOVE_VERSION221;
    if (versionLowerThan2_2_1(spec)) {
      strategy = DropIndexStrategy.LESS_THAN_VERSION221;
    }
    strategy.dropIndex(db, spec, collectionName);
  }

  private boolean shouldDropAllIndexes(final DBObject spec) {
    return spec.get("index").equals("*");
  }

  private boolean versionLowerThan2_2_1(final DBObject spec) {
    return spec.get("index") instanceof String;
  }
}
