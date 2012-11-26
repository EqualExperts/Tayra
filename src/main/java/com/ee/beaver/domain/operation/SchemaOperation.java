package com.ee.beaver.domain.operation;

import com.mongodb.DB;
import com.mongodb.DBObject;

public interface SchemaOperation {

  SchemaOperation NO_OP = new SchemaOperation() {
    @Override
    public void execute(final DB db, final DBObject spec) { }
      public String toString() { return "NO OPERATION"; };
    };

  void execute(final DB db, final DBObject spec);

}
