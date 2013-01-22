package com.ee.tayra.domain.operation;


import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

class DefaultSchemaOperation extends Operation {

  private final Mongo mongo;
  private final SchemaOperationsFactory schemaOperationsFactory;

  public DefaultSchemaOperation(final Mongo mongo,
      final SchemaOperationsFactory schemaOperationsFactory) {
    this.mongo = mongo;
    this.schemaOperationsFactory = schemaOperationsFactory;
  }

  @Override
  protected final void doExecute(final DBObject document) {
    final String ns = (String) document.get("ns");
    String[] dbInfo = ns.split("\\.");
    String dbName = dbInfo[0];
    String dbCommand = dbInfo[1];
    if ("$cmd".equals(dbCommand)) {
      DB db = mongo.getDB(dbName);
      DBObject spec = (DBObject) JSON.parse(document.get("o").toString());
      SchemaOperation schemaOperation = schemaOperationsFactory.from(spec);
      schemaOperation.doExecute(db, spec);
    }
  }

}
