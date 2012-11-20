package com.ee.beaver.domain.operation;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.Mongo;

public class Operations implements OperationsFactory {

  private final Mongo mongo;

  private final Map<String, Operation> operations;

  public Operations(final Mongo mongo) {
    this.mongo = mongo;
    operations = new HashMap<String, Operation>();
    fillOperations();
  }

  private void fillOperations() {
    operations.put("c", new DatabaseCommand(mongo));
    operations.put("i", new InsertDocument(mongo));
    operations.put("d", new DeleteDocument(mongo));
    operations.put("u", new UpdateDocument(mongo));
  }

  public final Operation get(final String opCode) {
    if (operations.containsKey(opCode)) {
      return operations.get(opCode);
    }
    return Operation.NO_OP;
  }
}
