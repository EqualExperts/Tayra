package com.ee.beaver.domain

import org.bson.types.BSONTimestamp
import spock.lang.*

import com.ee.beaver.domain.IteratorAlreadyClosed;
import com.ee.beaver.domain.MongoCollection;
import com.ee.beaver.domain.MongoCollectionIterator;
import com.ee.beaver.domain.NotALocalDB;
import com.ee.beaver.domain.NotAReplicaSetNode;
import com.ee.beaver.domain.Oplog;
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DB
import com.mongodb.DBCursor
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.QueryBuilder
import com.mongodb.util.JSON

public class OplogSpecs extends Specification {

	private static Mongo replicaSet
	private static final String HOST = "localhost"
	private static final int PORT = 27017
	private MongoCollection oplog
	private DB local

	def setupSpec()throws UnknownHostException,
			MongoException {
		replicaSet = new Mongo(HOST, PORT);
	}

	def cleanupSpec() {
		replicaSet.close();
	}

	def setup() {
		local = replicaSet.getDB("local");
		oplog = new Oplog(local);
	}

	def doesNotConnectToStandaloneMongoInstance() throws Exception {
		given: 'a standalone node'
			Mongo standalone = new Mongo(HOST, 27020);
			DB local = standalone.getDB("local");

		when: 'oplog created on Db of standalone'
			new Oplog(local);

		then: 'error message should be shown as'
			def problem = thrown(NotAReplicaSetNode)
			problem.message == "localhost is not a part of ReplicaSet";
	}


	def readsFirstDocument() throws UnknownHostException, MongoException {
		given: 'an iterator on the oplog'
			Iterator<DBObject> iterator = oplog.find();

		when: 'document of oplog is fetched'
			DBObject actualDBObject = iterator.next();

		then: 'it should be an appropriate document'
			actualDBObject instanceof DBObject
	}


	def doesNotAllowDocumentRemoval() {
		given: 'an iterator on the oplog'
			Iterator<DBObject> iterator = oplog.find();

		when: 'it tries to remove oplog document'
			iterator.remove();

		then: 'error message should be shown as'
			def problem = thrown(UnsupportedOperationException)
			problem.message == "remove document on oplog is not supported" ;
	}


	def permitsReadingFromLocalDBOnly() {
		given: 'a non local database'
			DB nonLocalDB = replicaSet.getDB("nonLocal");

		when: 'when oplog is created on that database'
			oplog = new Oplog(nonLocalDB);

		then: 'error message should be shown as'
			def problem = thrown(NotALocalDB)
			problem.message == "Not a local DB"
	}


	def itTailsOplog() {
		given: 'a tailable oplog iterator'
			boolean tailable = true;
			MongoCollectionIterator<DBObject> iterator = oplog.find(null, tailable);

		and: 'total count of oplog documents'
			long totalDocuments = local.getCollection("oplog.rs").count();
			long documentsRead = 0;

		when: 'all documents in the oplog are read'
			while (iterator.hasNext()) {
				iterator.next();
				documentsRead++;
				if (documentsRead == totalDocuments) {
					break;
				}
			}
			iterator.close();

		then: 'documents read must be equal to total oplog documents'
		documentsRead == totalDocuments;
	}


	def shoutsWhenQueryingWithAClosedIterator() {
		given: 'a tailable oplog iterator'
			boolean tailable = true;
			MongoCollectionIterator<DBObject> iterator = oplog.find(null, tailable);

		and: 'the iterator is closed'
			iterator.close();

		when:'the next document is queried'
			iterator.hasNext();

		then: 'error message should be shown as'
			def problem = thrown(IteratorAlreadyClosed)
			problem.message == "Iterator Already Closed"
	}


	def shoutsWhenFetchingWithAClosedIterator() {
		given: 'a tailable oplog iterator'
			boolean tailable = true;
			MongoCollectionIterator<DBObject> iterator = oplog.find(null, tailable);

		and: 'the iterator is closed'
			iterator.close();

		when:'the next document is fetched'
			iterator.next();

		then: 'error message should be shown as'
			def problem = thrown(IteratorAlreadyClosed)
			problem.message == "Iterator Already Closed"
	}

	def findsByQuery() throws Exception {
		given: ''
		 long currentTime = new Date().getTime();
		 BSONTimestamp ts = new BSONTimestamp((int) currentTime, 1);
		 DBObject query = BasicDBObjectBuilder
			.start()
			 .push('ts')
			  .add('$gte' , ts)
			 .pop()
			.get();
		 MongoCollectionIterator<DBObject> iterator = oplog.find(query);
		 long totalDocuments = local.getCollection("oplog.rs").count();
		 long documentsRead = 0;

		 when: ''
		 while (iterator.hasNext()) {
		  iterator.next();
		  documentsRead++;
		  if (documentsRead == totalDocuments) {
		   break;
		  }
		 }
		 iterator.close();

		 then: ''
		 documentsRead == totalDocuments
	}

	def findsADocumentByQuery() throws Exception {
		given: ''
		DBObject id = new BasicDBObjectBuilder()
				.start()
				 .add("_id", -1)
				.get();
		DBCursor dbCursor = local.getCollection("oplog.rs").find()
			   .sort((DBObject) id);
		DBObject dbObject = dbCursor.next();
		String data = dbObject.toString();

		DBObject timestamp = (DBObject) JSON.parse(data);
		DBObject query = new QueryBuilder()
					 .start()
					   .put("ts")
					   .greaterThan(timestamp.get("ts"))
					 .get();

		when: ''
		MongoCollectionIterator<DBObject> iterator = oplog.find(query);

		then: ''
		iterator.next() == dbCursor.next();
	}
}
