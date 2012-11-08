package com.ee.beaver.io;

import java.io.IOException;

import com.ee.beaver.domain.operation.Operations;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class OplogReplayer {

  private final Operations operations;

  public OplogReplayer(final Operations operations) {
    this.operations = operations;
  }

  public void replayDocument(final String document) throws IOException {
    DBObject mongoDocument = (DBObject) JSON.parse(document);
    final String operationCode = (String) mongoDocument.get("op");
    operations.get(operationCode).execute(mongoDocument);
  }
}
