package com.ee.tayra.fixtures;
import java.net.UnknownHostException;
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

public class AssertMongoFixture extends DoFixture {
	private final MongoClient src;
	private final MongoClient dest;

	public AssertMongoFixture(String srcHost, int srcPort, String destHost,
			int destPort) throws UnknownHostException {
		src = new MongoClient(srcHost, srcPort);
		dest = new MongoClient(destHost, destPort);
	}

	public Fixture runInDatabaseQueryAndCleanupDatabases(String database, String query, boolean cleanupDB) {
		DB srcDB = getDB(src, database);
		DB destDB = getDB(dest, database);
		Number srcResult = (Number) srcDB.eval(query);
		Number destResult = (Number) destDB.eval(query);
		if(cleanupDB) {
			cleanupDBs(srcDB, destDB);
		}
		Result result = new Result(srcResult.longValue(), destResult.longValue());
		return new SetFixture(Collections.singletonList(result));
	}

	private void cleanupDBs(DB srcDB, DB destDB) {
		srcDB.dropDatabase();
		destDB.dropDatabase();
	}
	
	public Fixture findDocumentsForCollectionInDatabaseWhereAndCleanupDatabases(String collection, String database, String where, boolean cleanupDatabase) {
		DBObject whereClause = where != null ? (DBObject) JSON.parse(where) : null;
		DBObject ignoreFields = (DBObject) JSON.parse("{ _id : 0 }");
		
		DB srcDB = getDB(src, database);
		DBCollection srcCollection = srcDB.getCollection(collection);
		List<String> srcDocs = getDocuments(srcCollection.find(whereClause, ignoreFields));
		
		DB destDB = getDB(dest, database);
		DBCollection destCollection = destDB.getCollection(collection);
		List<String> destDocs = getDocuments(destCollection.find(whereClause, ignoreFields));
		List<Result> results = allDocuments(srcDocs, destDocs);
		if(cleanupDatabase) {
			cleanupDBs(srcDB, destDB);
		}
		return new SetFixture(results);
	}
	
	private DB getDB(MongoClient mongo, String dbname) {
		return mongo.getDB(dbname);
	}
	
	private List<Result> allDocuments(List<String> srcDocs,
			List<String> destDocs) {
		List<Result> results = new ArrayList<Result>();
		List<String> target = srcDocs;
		if(srcDocs.size() > destDocs.size()) {
			target = srcDocs;
		} 
		if(srcDocs.size() < destDocs.size()) {
			target = destDocs;
		} 
		for (int index = 0; index < target.size(); index++) {
			results.add(new Result(srcDocs.get(index), destDocs.get(index)));
		}
		return results;
	}

	private List<String> getDocuments(DBCursor results) {
		List<String> documents = new ArrayList<String>();
		for(DBObject document : results) {
			documents.add(document.toString());
		}
		return documents;
	}

	
	@Override
	protected void tearDown() throws Exception {
		src.close();
		dest.close();
		super.tearDown();
	}
}
