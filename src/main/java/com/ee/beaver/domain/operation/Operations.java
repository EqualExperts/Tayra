package com.ee.beaver.domain.operation;

import com.mongodb.Mongo;

public class Operations {

  private Mongo mongo;

  public Operations(final Mongo mongo) {
    this.mongo = mongo;
  }

  public final Operation get(final String opCode) {
    if ("c".equals(opCode)) {
      return new CreateCollection(mongo);
    }
    if ("i".equals(opCode)) {
      return new InsertDocument();
    }
    return Operation.NO_OP;
  }
}
