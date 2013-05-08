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
          throw new OperationFailed(problem.getMessage());
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
