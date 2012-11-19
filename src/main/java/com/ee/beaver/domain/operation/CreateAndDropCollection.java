package com.ee.beaver.domain.operation;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

class CreateAndDropCollection implements Operation {

  private static final String MAX = "max";
  private static final String SIZE = "size";
  private static final String CAPPED = "capped";
  private final Mongo mongo;

  public CreateAndDropCollection(final Mongo mongo) {
    this.mongo = mongo;
  }

  @Override
  public final void execute(final DBObject document) {
    final String ns = (String) document.get("ns");
    String[] dbInfo = ns.split("\\.");
    String dbName = dbInfo[0];
    String dbCommand = dbInfo[1];
    if ("$cmd".equals(dbCommand)) {
      DB db = mongo.getDB(dbName);
      DBObject spec = (DBObject) JSON.parse(document.get("o").toString());
     
      if (spec.containsField("create")) {
        createCollection(db, spec);
      }

      if (spec.containsField("drop")) {
        dropCollection(db, spec);
      }
    }
  }

private void dropCollection(DB db, DBObject spec) {
	final String dropCollectionName = (String) spec.get("drop");
	if(! db.collectionExists(dropCollectionName)) {
      throw new DropCollectionFailed("Could Not Drop Collection " + dropCollectionName);
	}
	db.getCollection(dropCollectionName).drop();
}

private void createCollection(DB db, DBObject spec) {
	final String createCollectionName = (String) spec.get("create");
	DBObject options = new BasicDBObjectBuilder()
	  .start()
	  .add(CAPPED, spec.get(CAPPED))
	  .add(SIZE, spec.get(SIZE))
	  .add(MAX, spec.get(MAX))
	  .get();
	try {
	    db.createCollection(createCollectionName, options);
	} catch (Exception problem) {
	  throw new CreateCollectionFailed(problem.getMessage());
	}
}
}
