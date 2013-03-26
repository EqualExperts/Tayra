package com.ee.tayra.domain

import com.ee.tayra.domain.operation.RequiresMongoConnection
import com.ee.tayra.parameters.EnvironmentProperties
import com.mongodb.DB
import com.mongodb.DBCursor
import com.mongodb.DBObject
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.util.JSON

public class OplogSpecs extends RequiresMongoConnection {

	private static MongoClient replicaSet
	private static final String HOST = EnvironmentProperties.secureSrcNode
	private static final int PORT = EnvironmentProperties.secureSrcPort
	private static final String USERNAME = EnvironmentProperties.username
	private static final String PASSWORD = EnvironmentProperties.password
	private MongoCollection oplog
	private DB local
	private String query = null
	private boolean tailable = true

	def setupSpec()throws MongoException {
		replicaSet = new MongoClient(HOST, PORT)
	}

	def cleanupSpec() {
		replicaSet.close()
	}

	def setup() {
		replicaSet.getDB("admin").authenticate(USERNAME, PASSWORD.toCharArray())
		oplog = new Oplog(replicaSet)
		local = replicaSet.getDB("local")
	}

	def doesNotConnectToStandaloneMongoInstance() throws Exception {
		given: 'a standalone node'
			standalone.getDB('admin').authenticate(USERNAME, PASSWORD.toCharArray())
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

		and: 'we also fetch second document'
			document =  dbCursor.next()

		then: "iterator's current & cursor's current document should be same"
			iterator.next() == JSON.serialize(document);
	}


	def avoidsHingeDocumentToBeReadAgain() {
		given: 'last backup was taken'
			Iterator<String> previousIterator = oplog.find(null, tailable)
			String lastDocument = previousIterator.next()

		and: 'we have timestamp of last document'
			String timestamp = extractTimestamp(lastDocument)

		when: 'backup was performed again'
			Iterator<String> nextIterator = oplog.find(timestamp, tailable)
			String nextDocument = nextIterator.next()

		then: 'it continues from where it left'
			nextDocument == previousIterator.next()
	}

	private extractTimestamp(String document) {
		"{ " +  document.substring(document.indexOf("\"ts\""),
			document.indexOf("}") + 1) + " }"
	}

}
