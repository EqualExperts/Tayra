package com.ee.beaver.domain

import spock.lang.Specification

import com.ee.beaver.domain.operation.RequiresMongoConnection
import com.mongodb.DB
import com.mongodb.DBCursor
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.util.JSON

public class OplogSpecs extends RequiresMongoConnection {

	private static Mongo replicaSet
	private static final String HOST = "localhost"
	private static final int PORT = 27017
	private MongoCollection oplog
	private DB local
	private String query = null
	private boolean tailable = true

	def setupSpec()throws UnknownHostException,
			MongoException {
		replicaSet = new Mongo(HOST, PORT)
	}

	def cleanupSpec() {
		replicaSet.close()
	}

	def setup() {
		oplog = new Oplog(replicaSet)
		local = replicaSet.getDB("local")
	}

	def doesNotConnectToStandaloneMongoInstance() throws Exception {
		given: 'a standalone node'
			standalone.getDB('admin').authenticate('admin', 'admin'.toCharArray())
			DB local = standalone.getDB("local")

		when: 'oplog created on Db of standalone'
			new Oplog(standalone)
			
		then: 'error message should be shown as'
			def problem = thrown(NotAReplicaSetNode)
			problem.message == "node is not a part of ReplicaSet"
	}


	def readsFirstDocument() throws UnknownHostException, MongoException {
		given: 'an iterator on the oplog'
			Iterator<String> iterator = oplog.find(query, tailable)
			
		when: 'document of oplog is fetched'
			String actualDocument = iterator.next()

		then: 'it should be an appropriate document'
			actualDocument instanceof String
	}

	def doesNotAllowDocumentRemoval() {
		given: 'an iterator on the oplog'
			Iterator<String> iterator = oplog.find(query, tailable)

		when: 'it tries to remove oplog document'
			iterator.remove()

		then: 'error message should be shown as'
			def problem = thrown(UnsupportedOperationException)
			problem.message == "remove document on oplog is not supported" 
	}


	def itTailsOplog() {
		given: 'a tailable oplog iterator'
			MongoCollectionIterator<String> iterator = oplog.find(query, tailable)
		
		and: 'total count of oplog documents'
			long totalDocuments = local.getCollection("oplog.rs").count()
			long documentsRead = 0

		when: 'all documents in the oplog are read'
			while (iterator.hasNext()) {
				iterator.next()
				documentsRead++
				if (documentsRead == totalDocuments) {
					break
				}
			}
			iterator.close()

		then: 'documents read must be equal to total oplog documents'
		documentsRead == totalDocuments
	}


	def shoutsWhenQueryingWithAClosedIterator() {
		given: 'a tailable oplog iterator'
			MongoCollectionIterator<String> iterator = oplog.find(query, tailable)
			
		and: 'the iterator is closed'
			iterator.close()

		when:'the next document is queried'
			iterator.hasNext()

		then: 'error message should be shown as'
			def problem = thrown(IteratorAlreadyClosed)
			problem.message == "Iterator Already Closed"
	}


	def shoutsWhenFetchingWithAClosedIterator() {
		given: 'a tailable oplog iterator'
			MongoCollectionIterator<String> iterator = oplog.find(query, tailable)
			
		and: 'the iterator is closed'
			iterator.close()

		when:'the next document is fetched'
			iterator.next()

		then: 'error message should be shown as'
			def problem = thrown(IteratorAlreadyClosed)
			problem.message == "Iterator Already Closed"
	}


	def findsADocumentByQuery() throws Exception {
		given: 'we fetch the first document from mongo db'
			DBCursor dbCursor = local.getCollection("oplog.rs")
									 .find()
			DBObject document = dbCursor.next()

		when: 'query executes, iterator points to second document'
			MongoCollectionIterator<String> iterator = oplog.find(JSON.serialize(document), tailable)

		then: "iterator's current & cursor's current document should be same"
			iterator.next() == JSON.serialize(document);
	}
}
