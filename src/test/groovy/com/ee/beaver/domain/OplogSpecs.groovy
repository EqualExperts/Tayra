package com.ee.beaver.domain

import java.net.UnknownHostException;

import spock.lang.Specification;

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DB;
import com.mongodb.DBCursor
import com.mongodb.DBObject
import com.mongodb.Mongo;
import com.mongodb.MongoException;

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
		oplog = new Oplog(replicaSet);
		local = replicaSet.getDB("local");
	}

	def doesNotConnectToStandaloneMongoInstance() throws Exception {
		given: 'a standalone node'
			Mongo standalone = new Mongo(HOST, 27020);
			DB local = standalone.getDB("local");

		when: 'oplog created on Db of standalone'
			new Oplog(standalone);
			
		then: 'error message should be shown as'
			def problem = thrown(NotAReplicaSetNode)
			problem.message == "localhost is not a part of ReplicaSet";
	}


	def readsFirstDocument() throws UnknownHostException, MongoException {
		given: 'an iterator on the oplog'
			Iterator<String> iterator = oplog.find();
			
		when: 'document of oplog is fetched'
			String actualDocument = iterator.next();

		then: 'it should be an appropriate document'
			actualDocument instanceof String
	}	

	def doesNotAllowDocumentRemoval() {
		given: 'an iterator on the oplog'
			Iterator<String> iterator = oplog.find();

		when: 'it tries to remove oplog document'
			iterator.remove();

		then: 'error message should be shown as'
			def problem = thrown(UnsupportedOperationException)
			problem.message == "remove document on oplog is not supported" ;
	}

	
	def itTailsOplog() {
		given: 'a tailable oplog iterator'
			boolean tailable = true;
			MongoCollectionIterator<String> iterator = oplog.find(null, tailable);
		
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
			MongoCollectionIterator<String> iterator = oplog.find(null, tailable);
			
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
			MongoCollectionIterator<String> iterator = oplog.find(null, tailable);
			
		and: 'the iterator is closed'
			iterator.close();

		when:'the next document is fetched'
			iterator.next();

		then: 'error message should be shown as'
			def problem = thrown(IteratorAlreadyClosed)
			problem.message == "Iterator Already Closed"
	}


	def findsADocumentByQuery() throws Exception {
		given: 'we fetch the first document from mongo db'
			DBObject id = new BasicDBObjectBuilder()
							.start()
							 .add("_id", -1)
							.get();
			DBCursor dbCursor = local.getCollection("oplog.rs")
									 .find()
									 .sort((DBObject) id);
			DBObject document = dbCursor.next();

		when: 'query executes, iterator points to second document'
			MongoCollectionIterator<String> iterator = oplog.find(JSON.serialize(document));

		then: 'iterator\'s current & cursor\'s current document should be same'
			iterator.next() == JSON.serialize(dbCursor.next());
	}
}
