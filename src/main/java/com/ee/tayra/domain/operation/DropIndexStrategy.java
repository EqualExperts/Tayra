/*******************************************************************************
 * Copyright (c) 2013, Equal Experts Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation
 * are those of the authors and should not be interpreted as representing
 * official policies, either expressed or implied, of the Tayra Project.
 ******************************************************************************/
package com.ee.tayra.domain.operation;

import static fj.data.List.list;

import java.util.List;
import java.util.Set;

import com.mongodb.DB;
import com.mongodb.DBObject;

import fj.F;

public enum DropIndexStrategy {
  LESS_THAN_VERSION221 {

  @Override
  public void dropIndex(final DB db, final DBObject spec,
      final String collectionName) {
    List<DBObject> indexes = db.getCollection(collectionName)
         .getIndexInfo();
    String indexName = (String) spec.get("index");
    if (doesNotExist(indexes, indexName)) {
          throw new OperationFailed("Cannot drop index : "
            + indexName + " Index doesn't exist.");
        }
        db.getCollection(collectionName).dropIndex(indexName);
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
  },

  ABOVE_VERSION221 {

  @Override
  public void dropIndex(final DB db, final DBObject spec,
      final String collectionName) {
    List<DBObject> indexes = db.getCollection(collectionName)
        .getIndexInfo();
    DBObject indexObj = (DBObject) spec.get("index");
           toMongoFormat(indexObj);
           if (doesNotExist(indexes, indexObj)) {
             throw new OperationFailed("Cannot drop index : "
               + indexObj.toString() + " Index doesn't exist.");
           }
          db.getCollection(collectionName).dropIndex(indexObj);
  }
  private void toMongoFormat(final DBObject indexObj) {
    Set<String> indexKeys = indexObj.keySet();
      for (String indexKey : indexKeys) {
        Double indexValue = (Double) indexObj.get(indexKey);
        indexObj.put(indexKey, indexValue.intValue());
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
  };

  public abstract void dropIndex(DB db, DBObject spec, String collectionName);
}
