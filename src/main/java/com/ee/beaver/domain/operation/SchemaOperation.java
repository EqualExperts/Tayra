package com.ee.beaver.domain.operation;

import com.mongodb.DB;
import com.mongodb.DBObject;

public interface SchemaOperation {

  void execute(final DB db, final DBObject spec);

}
