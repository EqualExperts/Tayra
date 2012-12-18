package com.ee.beaver.domain.operation

import org.bson.types.ObjectId

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.CommandResult;
import com.mongodb.DBObject

class InsertDocumentSpecs extends RequiresMongoConnection {
	
	private String collectionName = 'home'
	private String prefixedCollectionName = 'home.test'
	private String anotherDb = 'mongoose'
	def operation
	def objId = new ObjectId('509754dd2862862d511f6b57')
	def name = '[Test Name]'
	
	def setup() {
		operation = new InsertDocument(standalone)
	}
	
	def cleanup() {
		standalone.getDB(dbName).getCollection(collectionName).drop()
		standalone.getDB(dbName).getCollection(prefixedCollectionName).drop()
		standalone.getDB(dbName).dropDatabase()
		standalone.getDB(anotherDb).dropDatabase()
	}
	
	private void assertThatDocumentIsPresentInCollection(String db, String collection, DBObject document) {
		standalone.getDB(db).getCollection(collection).findOne(document) == document
	}

	
	def insertsDocument() throws Exception {
		given: 'an oplog entry for insert, upsert'
			def o = BasicDBObjectBuilder
					.start()
						.add('_id', objId)
						.add('name', name)
					.get()
			def document = MongoUtils.insertDocument(dbName, collectionName, o) as String
		
		when: 'the operation runs'
			operation.execute(document as String)

		then: 'the document should exist'
			assertThatDocumentIsPresentInCollection(dbName, collectionName, o)
	}
	
	
	def insertsDocumentInAPrefixedCollection() throws Exception {
		given: 'an insert document oplog entry on a prefixed collection'
			def o = BasicDBObjectBuilder
					.start()
						.add('_id', objId)
						.add('name', name)
					.get()
			def document = MongoUtils.insertDocument(dbName, prefixedCollectionName, o) as String
		
		when: 'the operation runs'
			operation.execute(document)
		
		then: 'the document should exist'
			assertThatDocumentIsPresentInCollection(dbName, prefixedCollectionName, o)
	}
	
	
	def insertsDocumentInAnotherDatabase() throws Exception {
		given: 'an insert document oplog entry on another database'
			def o = BasicDBObjectBuilder
					.start()
						.add('_id', objId)
						.add('name', name)
					.get()
			def document = MongoUtils.insertDocument(anotherDb, collectionName, o) as String

		when: 'the operation runs'
			operation.execute(document)

		then: 'the document should exist'
			assertThatDocumentIsPresentInCollection(anotherDb, collectionName, o)
	}
	
	
	def insertsNestedDocument() {
		given: 'an insert document oplog entry for nested document'
			def o = BasicDBObjectBuilder
					.start()
						.add('_id', objId)
						.add('name', name)
						.push('address')
							.add('street', '[Some Street]')
							.add('city', '[Some City]')
							.add('country', '[CN]')
						.pop()
					.get()
			def document = MongoUtils.insertDocument(dbName, collectionName, o) as String

		when: 'the operation runs'
			operation.execute(document)

		then: 'the document should exist'
			assertThatDocumentIsPresentInCollection(dbName, collectionName, o)
	}
	
	
	def insertsDeeplyNestedDocument() {
		given: 'an insert document oplog entry for deeply nested document'
			def o = BasicDBObjectBuilder
					.start()
						.add('_id', objId)
						.add('name', name)
						.push('address')
							.add('street', '[Some Street]')
							.add('city', '[Some City]')
							.add('country', '[CN]')
							.push('geocode')
								.add('lat', '[Some Lat]')
								.add('long', '[Some Long]')
							.pop()
						.pop()
					.get()
			def document = MongoUtils.insertDocument(dbName, collectionName, o) as String

		when: 'the operation runs'
			operation.execute(document)

		then: 'the document should exist'
			assertThatDocumentIsPresentInCollection(dbName, collectionName, o)
	}
	
	
	def shoutsWhenDuplicateDocumentIsInserted() throws Exception {
		given: 'an insert document oplog entry'
			def o = BasicDBObjectBuilder
					.start()
						.add('_id', objId)
						.add('name', name)
					.get()
			def document = MongoUtils.insertDocument(dbName, collectionName, o) as String

		and: 'the document already exists'
			operation.execute(document)

		when: 'the operation runs'
			operation.execute(document)
			
		then: 'it complains that the document cannot be inserted again'
			def problem = thrown(InsertFailed)
			problem.message == '''E11000 duplicate key error index: beaver.home.$_id_  dup key: { : ObjectId('509754dd2862862d511f6b57') }'''
	}
}
