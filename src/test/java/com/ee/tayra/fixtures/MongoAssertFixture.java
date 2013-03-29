package com.ee.tayra.fixtures;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import fit.Fixture;
import fitlibrary.DoFixture;
import fitlibrary.SetFixture;

public class MongoAssertFixture extends DoFixture {

  private static final DBObject IGNORE_FIELDS =
                              (DBObject) JSON.parse("{ _id : 0 }");
  private final MongoClient src;
  private final MongoClient tgt;

  public MongoAssertFixture(final MongoClient src, final MongoClient tgt) {
    this.src = src;
    this.tgt = tgt;
  }

  public final Fixture runInDatabaseQueryAndCleanupDatabases(
  final String database, final String query, final boolean cleanupDB) {
    DB srcDB = getDB(src, database);
    DB tgtDB = getDB(tgt, database);
    Number srcResult = (Number) srcDB.eval(query);
    Number tgtResult = (Number) tgtDB.eval(query);
    if (cleanupDB) {
      cleanupDBs(srcDB, tgtDB);
    }
    Result result = new Result(srcResult.longValue(), tgtResult.longValue());
    return new SetFixture(Collections.singletonList(result));
  }

  private void cleanupDBs(final DB srcDB, final DB tgtDB) {
    srcDB.dropDatabase();
    tgtDB.dropDatabase();
  }

  public final Fixture
  findDocumentsForCollectionInDatabaseWhereAndCleanupDatabases(
  final String collection, final String database,
  final String where, final boolean cleanupDatabase) {
    DBObject predicates = null;
    if (where != null) {
        predicates = (DBObject) JSON.parse(where);
    }
    DB srcDB = getDB(src, database);
    List<String> srcDocs =
        documentsInCollection(srcDB, collection, predicates);

    DB tgtDB = getDB(tgt, database);
    List<String> destDocs =
        documentsInCollection(tgtDB, collection, predicates);

    List<Result> results = allDocuments(srcDocs, destDocs);
    if (cleanupDatabase) {
      cleanupDBs(srcDB, tgtDB);
    }
    return new SetFixture(results);
  }

  private List<String> documentsInCollection(
  final DB db, final String collection, final DBObject where) {
    DBCollection aCollection = db.getCollection(collection);
    return documentsFrom(aCollection.find(where, IGNORE_FIELDS));
  }

  private DB getDB(final MongoClient mongo, final String dbname) {
    return mongo.getDB(dbname);
  }

  private List<Result> allDocuments(final List<String> srcDocs,
  final List<String> tgtDocs) {
    List<Result> results = new ArrayList<Result>();
    List<String> target = srcDocs;
    if (srcDocs.size() > tgtDocs.size()) {
      target = srcDocs;
    }
    if (srcDocs.size() < tgtDocs.size()) {
      target = tgtDocs;
    }
    for (int index = 0; index < target.size(); index++) {
      String srcValue = getValueOrNullAt(srcDocs, index);
      String tgtValue = getValueOrNullAt(tgtDocs, index);
      results.add(new Result(srcValue, tgtValue));
    }
    return results;
  }

  private String getValueOrNullAt(final List<String> docs,
  final int index) {
    if (index > docs.size() - 1) {
      return null;
    } else {
      return docs.get(index);
    }
  }

  private List<String> documentsFrom(final DBCursor results) {
    List<String> documents = new ArrayList<String>();
    for (DBObject document : results) {
      documents.add(document.toString());
    }
    return documents;
  }
}
