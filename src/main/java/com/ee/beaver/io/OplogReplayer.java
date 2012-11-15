package com.ee.beaver.io;

import com.ee.beaver.domain.operation.OperationsFactory;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class OplogReplayer {

  private final OperationsFactory operations;

  public OplogReplayer(final OperationsFactory operations) {
    this.operations = operations;
  }

  public void replayDocument(final String document) {
    DBObject mongoDocument = (DBObject) JSON.parse(document);
    final String operationCode = (String) mongoDocument.get("op");
    operations.get(operationCode).execute(mongoDocument);
  }
}
