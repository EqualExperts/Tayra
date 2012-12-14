package com.ee.beaver.domain.operation;

import java.util.List;
import com.mongodb.DB;
import com.mongodb.DBObject;
import fj.F;
import static fj.data.List.list;

public class DropIndex implements SchemaOperation {

  @Override
  public final void doExecute(final DB db, final DBObject spec) {
    String collectionName = (String) spec.get("deleteIndexes");
    List<DBObject> indexes = db.getCollection(collectionName)
                                        .getIndexInfo();
    String indexName = (String) spec.get("index");
    if (doesNotExist(indexes, indexName)) {
      throw new DropIndexFailed("Cannot drop index : " + indexName
      + " Index doesn't exist.");
    }
    db.getCollection(collectionName).dropIndex(indexName);
  }

private boolean doesNotExist(final List<DBObject> existingIndexList,
  final String indexName) {
  DBObject [] indexes = new DBObject[existingIndexList.size()];
  existingIndexList.toArray(indexes);
  return !list(indexes).exists(new F<DBObject, Boolean>() {
    @Override
    public Boolean f(final DBObject document) {
      String name = (String) document.get("name");
      return name.equalsIgnoreCase(indexName);
      }
    });
  }
}
