package com.ee.beaver.io

import org.bson.types.ObjectId

import spock.lang.*

import com.ee.beaver.domain.MongoCollection
import com.ee.beaver.domain.MongoCollectionIterator
import com.ee.beaver.domain.operation.MongoUtils
import com.mongodb.BasicDBObjectBuilder

public class OplogReaderSpecs extends Specification{

	private MongoCollection mockOplogCollection

	private MongoCollectionIterator<String> mockOplogCollectionIterator
	
	private CollectionReader reader
	private String dbName = 'beaver'
	private String collectionName = 'home'
	private String name = '[Test Name]'
	private String fromTimestamp = '{\"ts\" : { \"$ts\" : 1354096315 , \"$inc\" : 10}}'
	def objId

	def setup() {
		mockOplogCollection = Stub(MongoCollection)
		mockOplogCollectionIterator = Stub(MongoCollectionIterator)
		mockOplogCollection.find(fromTimestamp, false) >> mockOplogCollectionIterator
		reader = new OplogReader(mockOplogCollection, fromTimestamp, false)
		objId = new ObjectId()
	}


	def readsACreateCollectionOperationDocument() {
		given: 'a create collection oplog entry'
			def document = MongoUtils.createCollection(dbName, collectionName) as String
		
		and: 'oplog iterator returns the document'
			mockOplogCollectionIterator.hasNext() >> true
			mockOplogCollectionIterator.next() >> document

		when: 'reader reads that document'
			String oplogDocumentString = reader.readDocument()

		then: 'it should read expected document'
			oplogDocumentString == '{"ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}","h":"3493050463814977392","op":"c","ns":"' + dbName + '.$cmd","o":"{ \\"create\\" : \\"' + collectionName + '\\" , \\"capped\\" : false , \\"size\\" :  null  , \\"max\\" :  null }"}'
	}


	def readsAnInsertOperationDocument() {
		given: 'an insert document oplog entry'
			def o = new BasicDBObjectBuilder()
						.start()
							.add('_id', objId)
							.add('name', name)
						.get()
			def document = MongoUtils.insertDocument(dbName, collectionName, o) as String
			
		and: 'oplog iterator returns the document'
			mockOplogCollectionIterator.hasNext() >> true
			mockOplogCollectionIterator.next() >> document

		when: 'reader reads that document'
			String oplogDocumentString = reader.readDocument()

		then: 'it should read the expected document'
			oplogDocumentString == '{"ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}","h":"3493050463814977392","op":"i","ns":"' + "$dbName.$collectionName" + '","o":"{ \\"_id\\" : { \\"$oid\\" : \\"' + objId + '\\"} , \\"name\\" : \\"' + name + '\\"}"}'
	}


	def readsAnUpdateOperationDocument() {
		given: 'an update document oplog entry'
			def o2 = new BasicDBObjectBuilder()
						.start()
							.add('_id', objId)
						.get()

			def o = new BasicDBObjectBuilder()
						.start()
							.add('name', name)
						.get()
			def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as String
			
		and: 'oplog iterator returns the document'
			mockOplogCollectionIterator.hasNext() >> true
			mockOplogCollectionIterator.next() >> document

		when: 'reader reads that document'
			String oplogDocumentString = reader.readDocument()

		then: 'it should read the expected document'
			oplogDocumentString == '{"ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}","h":"3493050463814977392","op":"u","ns":"' + "$dbName.$collectionName" + '","o2":"{ \\"_id\\" : { \\"$oid\\" : \\"' + objId + '\\"}}","o":"{ \\"name\\" : \\"'+name+'\\"}"}'
	}


	def readsARemoveOperationDocument() {
		given: 'a delete document oplog entry'
			def o = new BasicDBObjectBuilder()
						.start()
							.add('_id', objId)
						.get()
			def document = MongoUtils.deleteDocument(dbName, collectionName,o) as String
			
		and: 'oplog iterator returns the document'
			mockOplogCollectionIterator.hasNext() >> true
			mockOplogCollectionIterator.next() >> document

		when:'reader reads that document'
			String oplogDocumentString = reader.readDocument()

		then:'it should read the expected document'
			oplogDocumentString == '{"ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}","h":"3493050463814977392","op":"d","ns":"' + "$dbName.$collectionName" + '","b":true,"o":"{ \\"_id\\" : { \\"$oid\\" : \\"' + objId + '\\"}}"}'
	}

	def readsADropCollectionOperationDocument() {
		given: 'a drop collection oplog entry'
			def document = MongoUtils.dropCollection(dbName, collectionName) as String
			
		and: 'oplog iterator returns the document'
			mockOplogCollectionIterator.hasNext() >> true
			mockOplogCollectionIterator.next() >> document

		when: 'reader reads that document'
			String oplogDocumentString = reader.readDocument()

		then: 'it should read the expected document'
			oplogDocumentString == '{"ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}","h":"3493050463814977392","op":"c","ns":"' + "$dbName" +'.$cmd","o":"{ \\"drop\\" : \\"'+ collectionName +'\\"}"}'
	}


	def shoutsWhenQueryingForDocumentWithAClosedReader() {
		when: 'reader is closed'
			reader.close()

		and: 'reader tries to query for a document'
			reader.hasDocument()

		then: 'error message should be shown as'
			def problem = thrown(ReaderAlreadyClosed)
			problem.message == "Reader Already Closed"
	}


	def shoutsWhenFetchingForDocumentWithAClosedReader() {
		when: 'reader is closed'
			reader.close()

		and: 'reader tries to read a document'
			reader.readDocument()

		then: 'error message should be shown as'
			def problem = thrown(ReaderAlreadyClosed)
			problem.message == "Reader Already Closed"
	}

}
