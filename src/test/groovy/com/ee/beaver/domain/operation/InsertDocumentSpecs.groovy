package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.fail

import org.bson.types.ObjectId
import org.junit.After
import org.junit.Before
import org.junit.Test

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

class InsertDocumentSpecs extends RequiresMongoConnection {
	
	private String collectionName = 'home'
	private String prefixedCollectionName = 'home.test'
	private String anotherDb = 'mongoose'
	def operation
	def objId = new ObjectId('509754dd2862862d511f6b57')
	def name = '[Test Name]'
	
	@Before
	public void given() {
		operation = new InsertDocument(standalone)
	}
	
	@After
	public void cleanUp() {
		standalone.getDB(dbName).getCollection(collectionName).drop()
		standalone.getDB(dbName).getCollection(prefixedCollectionName).drop()
		standalone.getDB(anotherDb).dropDatabase()
	}
	
	private void assertThatDocumentIsPresentInCollection(String db, String collection, DBObject document) {
		assertThat standalone.getDB(db).getCollection(collection).findOne(document), is(document)
	}

	@Test
	public void insertsDocument() throws Exception {
		//Given
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
						.add('name', name)
					.get()
		def document = MongoUtils.insertDocument(dbName, collectionName, o) as DBObject
		
		//When
		operation.execute(document)

		//Then
		assertThatDocumentIsPresentInCollection(dbName, collectionName, o)
	}
	
	@Test
	public void insertsDocumentInAPrefixedCollection() throws Exception {
		//Given
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
						.add('name', name)
					.get()
		def document = MongoUtils.insertDocument(dbName, prefixedCollectionName, o) as DBObject
		
		//When
		operation.execute(document)
		
		//Then
		assertThatDocumentIsPresentInCollection(dbName, prefixedCollectionName, o)
	}
	
	@Test
	public void insertsDocumentInAnotherDatabase() throws Exception {
		//Given
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
						.add('name', name)
					.get()
		def document = MongoUtils.insertDocument(anotherDb, collectionName, o) as DBObject

		//When
		operation.execute(document)

		//Then
		assertThatDocumentIsPresentInCollection(anotherDb, collectionName, o)
	}
	
	@Test
	public void insertsNestedDocument() {
		//Given
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
						.add('name', name)
						.push('address')
							.add('street', '[Some Street]')
							.add('city', '[Some City]')
							.add('country', '[CN]')
						.pop()
					.get()
		def document = MongoUtils.insertDocument(dbName, collectionName, o) as DBObject

		//When
		operation.execute(document)

		//Then
		assertThatDocumentIsPresentInCollection(dbName, collectionName, o)
	}
	
	@Test
	public void insertsDeeplyNestedDocument() {
		//Given
		def o = new BasicDBObjectBuilder()
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
		def document = MongoUtils.insertDocument(dbName, collectionName, o) as DBObject

		//When
		operation.execute(document)

		//Then
		assertThatDocumentIsPresentInCollection(dbName, collectionName, o)
	}
	
	@Test
	public void shoutsWhenDuplicateDocumentIsInserted() throws Exception {
		//Given
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
						.add('name', name)
					.get()
		def document = MongoUtils.insertDocument(dbName, collectionName, o) as DBObject

		operation.execute(document)

		//When
		try {
			operation.execute(document)
			fail("Should not insert document with duplicate keys: $objId, already exists!")
		} catch (InsertFailed problem) {
		  //Then
		  assertThat problem.message, is('''E11000 duplicate key error index: beaver.home.$_id_  dup key: { : ObjectId('509754dd2862862d511f6b57') }''')
		}
	}
}
