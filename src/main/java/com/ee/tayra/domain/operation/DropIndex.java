package com.ee.tayra.domain.operation;

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
      if (versionLowerThan2_2_1(spec)) {
        String indexName = (String) spec.get("index");
        if (doesNotExist(indexes, indexName)) {
          throw new DropIndexFailed("Cannot drop index : "
            + indexName + " Index doesn't exist.");
        }
        db.getCollection(collectionName).dropIndex(indexName);
      } else {
           DBObject indexObj = (DBObject) spec.get("index");
           toMongoFormat(indexObj);
           if (doesNotExist(indexes, indexObj)) {
             throw new DropIndexFailed("Cannot drop index : "
               + indexObj.toString() + " Index doesn't exist.");
           }
          db.getCollection(collectionName).dropIndex(indexObj);
        }
    }
  }

  private boolean versionLowerThan2_2_1(final DBObject spec) {
    return spec.get("index") instanceof String;
  }

  private void toMongoFormat(final DBObject indexObj) {
    Set<String> indexKeys = indexObj.keySet();
      for (String indexKey : indexKeys) {
        Double indexValue = (Double) indexObj.get(indexKey);
        indexObj.put(indexKey, indexValue.intValue());
      }
  }

  private boolean doesNotExist(final List<DBObject> existingIndexList,
      final String indexName) {
    DBObject[] indexes = new DBObject[existingIndexList.size()];
    existingIndexList.toArray(indexes);
    return !list(indexes).exists(new F<DBObject, Boolean>() {
      @Override
      public Boolean f(final DBObject document) {
        String keyName = (String) document.get("name");
        return keyName.equals(indexName);
      }
    });
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
