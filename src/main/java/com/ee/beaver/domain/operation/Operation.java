package com.ee.beaver.domain.operation;

import com.mongodb.DBObject;

public interface Operation {

  Operation NO_OP = new Operation() {
  @Override
  public void execute(final DBObject document) { }
    public String toString() { return "NO OPERATION"; };
  };

  void execute(DBObject document);
}
