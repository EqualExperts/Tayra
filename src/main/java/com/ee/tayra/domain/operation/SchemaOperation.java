package com.ee.tayra.domain.operation;

import com.mongodb.DB;
import com.mongodb.DBObject;

public interface SchemaOperation {

  SchemaOperation NO_OP = new SchemaOperation() {
    @Override
    public void doExecute(final DB db, final DBObject spec) { }
      public String toString() { return "NO OPERATION"; };
    };

  void doExecute(final DB db, final DBObject spec);

}
