package com.ee.beaver.domain.operation;

import java.util.List;
import java.util.Set;

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
    if (spec.get("index").equals("*")) {
      db.getCollection(collectionName).dropIndexes();
    } else {
      DBObject indexObj = (DBObject) spec.get("index");
      Set<String> indexKeys = indexObj.keySet();
      for (String indexKey : indexKeys) {
        Double indexValue = (Double) indexObj.get(indexKey);
        indexObj.put(indexKey, indexValue.intValue());
      }
      if (doesNotExist(indexes, indexObj)) {
        throw new DropIndexFailed("Cannot drop index : "
            + indexObj.toString() + " Index doesn't exist.");
      }
      db.getCollection(collectionName).dropIndex(indexObj);
    }
  }

  private boolean doesNotExist(final List<DBObject> existingIndexList,
      final DBObject indexObj) {
    DBObject[] indexes = new DBObject[existingIndexList.size()];
    existingIndexList.toArray(indexes);
    return !list(indexes).exists(new F<DBObject, Boolean>() {
      @Override
      public Boolean f(final DBObject document) {
        DBObject keyObj = (DBObject) document.get("key");
        return keyObj.equals(indexObj);
      }
    });
  }
}
