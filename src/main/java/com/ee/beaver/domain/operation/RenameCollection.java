package com.ee.beaver.domain.operation;

import com.mongodb.DB;
import com.mongodb.DBObject;

public class RenameCollection implements SchemaOperation {

  @Override
  public final void doExecute(final DB db, final DBObject spec) {
    String collectionName = extractSourceCollectionName(spec);
    String targetCollectionName = extractTargetCollectionName(spec);
    try {
          if (spec.get("dropTarget") == null) {
            db.getCollection(collectionName).rename(targetCollectionName);
          } else {
            DBObject dropTarget = (DBObject) spec.get("dropTarget");
            db.getCollection(collectionName).rename(targetCollectionName,
                    (Boolean) dropTarget.get("dropTarget"));
          }
        } catch (Exception problem) {
          throw new RenameCollectionFailed(problem.getMessage());
      }
  }

  private String extractTargetCollectionName(final DBObject spec) {
    String targetName = (String) spec.get("to");
    int index = targetName.indexOf(".");
    return targetName.substring(index + 1, targetName.length());
  }

  private String extractSourceCollectionName(final DBObject spec) {
    String renameCollection = (String) spec.get("renameCollection");
    int index = renameCollection.indexOf(".");
    return renameCollection.substring(index + 1, renameCollection.length());
  }
}
