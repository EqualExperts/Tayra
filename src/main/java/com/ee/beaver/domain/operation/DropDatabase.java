package com.ee.beaver.domain.operation;

import java.util.List;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class DropDatabase implements SchemaOperation {

  private final Mongo mongo;

  public DropDatabase(final Mongo mongo) {
    this.mongo = mongo;
  }

  @Override
  public final void execute(final DB db, final DBObject spec) {
    List<String> databases = mongo.getDatabaseNames();
    if (!databases.contains(db.getName())) {
      throw new DropDatabaseFailed("Could Not Drop Database " + db.getName());
    }
    db.dropDatabase();
  }

}
