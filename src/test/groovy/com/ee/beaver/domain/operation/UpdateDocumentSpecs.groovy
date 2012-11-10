package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import org.bson.types.BSONTimestamp
import org.bson.types.ObjectId;
import org.junit.After
import org.junit.Before;
import org.junit.Ignore
import org.junit.Test
import static org.junit.Assert.fail

class UpdateDocumentSpecs extends RequiresMongoConnection {
	private String collectionName = 'home'
	private String prefixedCollectionName = 'home.test'
	private String anotherDb = 'mongoose'
	def operation
	def objId = new ObjectId('509754dd2862862d511f6b57')
	def nestedObjId = new ObjectId('509754dd2862862d511f6b58')
	def name = '[Test Name]'
	def documentToBeUpdated
	def nestedDocumentToBeUpdated
	
	@Before
	public void givenDocumentToBeUpdatedExistsInTargetDB() {
		documentToBeUpdated = new BasicDBObjectBuilder().start()
			.add('_id', objId)
			.add('name', name)
			.get()
			
		nestedDocumentToBeUpdated = new BasicDBObjectBuilder().start()
			.add('_id', nestedObjId)
			.add('name', name)
			.push('address')
				.add('street', '[Some Street]')
				.add('city', '[Some City]')
				.add('country', '[CN]')
			.pop()
			.get()
		standalone.getDB(db).getCollection(collectionName).insert(documentToBeUpdated)
		standalone.getDB(db).getCollection(prefixedCollectionName).insert(documentToBeUpdated)
		standalone.getDB(anotherDb).getCollection(collectionName).insert(documentToBeUpdated)
		standalone.getDB(db).getCollection(collectionName).insert(nestedDocumentToBeUpdated)
		
		operation = new UpdateDocument(standalone)
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
	public void updatesDocument() throws Exception {
		//Given
		def oplogDocument = new UpdateDocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'u',
			ns : "$db.$collectionName",
			o2 : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.get(),
				
			o : new BasicDBObjectBuilder().start()
				.add('name', "[Test Name 2]")
				.get()
		)
		
		//When
		operation.execute(oplogDocument as DBObject)

		//Then
		def expectedDocument = new BasicDBObjectBuilder()
		.start()
		.add('_id', objId)
		.add('name', "[Test Name 2]")
		.get()
		
		assertThatDocumentIsPresentInCollection(db, collectionName, expectedDocument)
	}
	
	@Test
	public void updatesDocumentInAPrefixedCollection() throws Exception {
		//Given
		def oplogDocument = new UpdateDocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'u',
			ns : "$db.$prefixedCollectionName",
			o2 : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.get(),
				
			o : new BasicDBObjectBuilder().start()
				.add('name', "[Test Name 2]")
				.get()
		)
		
		//When
		operation.execute(oplogDocument as DBObject)

		//Then
		def expectedDocument = new BasicDBObjectBuilder()
		.start()
		.add('_id', objId)
		.add('name', "[Test Name 2]")
		.get()
		
		assertThatDocumentIsPresentInCollection(db, prefixedCollectionName, expectedDocument)
	}
	
	@Test
	public void updatesDocumentInAnotherDatabase() throws Exception {
		//Given
		def oplogDocument = new UpdateDocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'u',
			ns : "$anotherDb.$collectionName",
			o2: new BasicDBObjectBuilder().start()
					.add('_id', objId)
					.get(),
			o : new BasicDBObjectBuilder().start()
					.add('_id', objId)
					.add('name', "[Test Name 2]")
					.get()
		)

		//When
		operation.execute(oplogDocument as DBObject)

		//Then
		def expectedDocument = new BasicDBObjectBuilder().start()
			.add('_id', objId)
			.add('name', "[Test Name 2]")
			.get()
		assertThatDocumentIsPresentInCollection(anotherDb, collectionName, expectedDocument)
	}
	
	@Test
	public void updatesNestedDocument() {
		//Given
		def oplogDocument = new UpdateDocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'u',
			ns : "$db.$collectionName",
			o2: new BasicDBObjectBuilder().start()
				.add('_id', nestedObjId)
				.get(),
			o : new BasicDBObjectBuilder().start()
				.add('_id', nestedObjId)
				.add('name', name)
				.push('address')
					.add('street', '[Any Street]')
					.add('city', '[Any City]')
					.add('country', '[COUNT]')
				.pop()
				.get()
		)

		//When
		operation.execute(oplogDocument as DBObject)

		//Then
		def expectedDocument = new BasicDBObjectBuilder().start()
			.add('_id', nestedObjId)
			.add('name', name)
			.push('address')
				.add('street', '[Any Street]')
				.add('city', '[Any City]')
				.add('country', '[COUNT]')
			.pop()
			.get()
		assertThatDocumentIsPresentInCollection(db, collectionName, expectedDocument)
	}
	
	@Test
	public void shoutsWhenDocumentToUpdateDoesNotExistInTarget() throws Exception {
		//Given
		def objId = new ObjectId('509e8839f91e1d01ec6dfb50')
		def oplogDocument = new UpdateDocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'u',
			ns : "$db.$collectionName",
			o2 : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.get(),
				
			o : new BasicDBObjectBuilder().start()
				.add('name', "[Test Name 2]")
				.get()
		)

		//When
		try {
			operation.execute(oplogDocument as DBObject)
			fail("Should not update document that does not exist")
		} catch (UpdateFailed problem) {
		  //Then
		  assertThat problem.message, is('Document does not exist { \"_id\" : { \"$oid\" : \"509e8839f91e1d01ec6dfb50\"}}')
		}
	}
}
