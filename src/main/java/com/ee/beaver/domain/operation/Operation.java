package com.ee.beaver.domain.operation;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public abstract class Operation {

  public static final Operation NO_OP = new Operation() {
    @Override
    public void doExecute(final DBObject document) { }
    public String toString() { return "NO OPERATION"; };
  };

  public void execute(final String document) {
    DBObject mongoDocument = (DBObject) JSON.parse(document);
    doExecute(mongoDocument);
  }

  protected abstract void doExecute(DBObject document);
}
