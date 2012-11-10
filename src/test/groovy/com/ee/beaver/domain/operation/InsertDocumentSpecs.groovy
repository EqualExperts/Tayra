package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import org.bson.types.BSONTimestamp
import org.bson.types.ObjectId;
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import static org.junit.Assert.fail

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
		standalone.getDB(db).getCollection(collectionName).drop()
		standalone.getDB(db).getCollection(prefixedCollectionName).drop()
		standalone.getDB(anotherDb).dropDatabase()
	}
	
	private void assertThatDocumentIsPresentInCollection(String db, String collection, DBObject document) {
		assertThat standalone.getDB(db).getCollection(collection).findOne(document), is(document)
	}

	@Test
	public void insertsDocument() throws Exception {
		//Given
		def oplogDocument = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'i',
			ns : "$db.$collectionName",
			o : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.add('name', name)
				.get()
		)

		//When
		operation.execute(oplogDocument as DBObject)

		//Then
		def expectedDocument = new BasicDBObjectBuilder()
			.start()
			.add('_id', objId)
			.add('name', name)
			.get()
			
		assertThatDocumentIsPresentInCollection(db, collectionName, expectedDocument)
	}
	
	@Test
	public void insertsDocumentInAPrefixedCollection() throws Exception {
		def oplogDocument = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'i',
			ns : "$db.$prefixedCollectionName",
			o : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.add('name', name)
				.get()
		)
		
		//When
		operation.execute(oplogDocument as DBObject)
		
		//Then
		def expectedDocument = new BasicDBObjectBuilder().start()
			.add('_id', objId)
			.add('name', name)
			.get()

		assertThatDocumentIsPresentInCollection(db, prefixedCollectionName, expectedDocument)
	}
	
	@Test
	public void insertsDocumentInAnotherDatabase() throws Exception {
		//Given
		def oplogDocument = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'i',
			ns : "$anotherDb.$collectionName",
			o : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.add('name', name)
				.get()
		)

		//When
		operation.execute(oplogDocument as DBObject)

		//Then
		def expectedDocument = new BasicDBObjectBuilder().start()
			.add('_id', objId)
			.add('name', name)
			.get()
		assertThatDocumentIsPresentInCollection(anotherDb, collectionName, expectedDocument)    
	}
	
	@Test
	public void insertsNestedDocument() {
		//Given
		def oplogDocument = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'i',
			ns : "$db.$collectionName",
			o : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.add('name', name)
				.push('address')
					.add('street', '[Some Street]')
					.add('city', '[Some City]')
					.add('country', '[CN]')
				.pop()
				.get()
		)

		//When
		operation.execute(oplogDocument as DBObject)

		//Then
		def expectedDocument = new BasicDBObjectBuilder().start()
			.add('_id', objId)
			.add('name', name)
			.push('address')
				.add('street', '[Some Street]')
				.add('city', '[Some City]')
				.add('country', '[CN]')
			.pop()
			.get()
		assertThatDocumentIsPresentInCollection(db, collectionName, expectedDocument)
	}
	
	@Test
	public void insertsDeeplyNestedDocument() {
		//Given
		def oplogDocument = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'i',
			ns : "$db.$collectionName",
			o : new BasicDBObjectBuilder().start()
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
		)

		//When
		operation.execute(oplogDocument as DBObject)

		//Then
		def expectedDocument = new BasicDBObjectBuilder().start()
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
		assertThatDocumentIsPresentInCollection(db, collectionName, expectedDocument)
	}
	
	@Test
	public void shoutsWhenDuplicateDocumentIsInserted() throws Exception {
		//Given
		def oplogDocument = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'i',
			ns : "$db.$collectionName",
			o : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.add('name', name)
				.get()
		)

		operation.execute(oplogDocument as DBObject)

		//When
		try {
			operation.execute(oplogDocument as DBObject)
			fail("Should not insert document with duplicate keys: $objId, already exists!")
		} catch (InsertFailed problem) {
		  //Then
		  assertThat problem.message, is('''E11000 duplicate key error index: beaver.home.$_id_  dup key: { : ObjectId('509754dd2862862d511f6b57') }''')
		}
	}
}
