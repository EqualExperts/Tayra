package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.fail

import org.bson.types.ObjectId
import org.junit.After
import org.junit.Before
import org.junit.Test

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

class UpdateDocumentSpecs extends RequiresMongoConnection {
	
	private String collectionName = 'home'
	private String prefixedCollectionName = 'home.test'
	private String anotherDb = 'mongoose'
	def operation
	def objId = new ObjectId('509754dd2862862d511f6b57')
	def nestedObjId = new ObjectId('509754dd2862862d511f6b58')
	def name = '[Test Name]'
	def updatedName = '[Test Name 2]'
	def documentToBeUpdated
	def nestedDocumentToBeUpdated
	
	@Before
	public void givenDocumentToBeUpdatedExistsInTargetDB() {
		documentToBeUpdated = new BasicDBObjectBuilder()
								.start()
									.add('_id', objId)
									.add('name', name)
								.get()
			
		nestedDocumentToBeUpdated = new BasicDBObjectBuilder()
									.start()
										.add('_id', nestedObjId)
										.add('name', name)
										.push('address')
											.add('street', '[Some Street]')
											.add('city', '[Some City]')
											.add('country', '[CN]')
										.pop()
									.get()
									
		insertIn(dbName, collectionName, documentToBeUpdated)
		insertIn(dbName, prefixedCollectionName, documentToBeUpdated)
		insertIn(anotherDb, collectionName, documentToBeUpdated)
		insertIn(dbName, collectionName, nestedDocumentToBeUpdated)
		operation = new UpdateDocument(standalone)
	}
	
	private void insertIn(String db, String collectionName, BasicDBObject documentToBeUpdated) {
		standalone.getDB(db).getCollection(collectionName).insert(documentToBeUpdated)
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
	public void updatesDocument() throws Exception {
		//Given
		def o2 = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
					.get()
					
		def o = new BasicDBObjectBuilder()
					.start()
						.add('name', updatedName)
					.get()
		
		def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as DBObject
		
		//When
		operation.execute(document)

		//Then
		def expectedDocument = new BasicDBObjectBuilder()
									.start()
										.add('_id', objId)
										.add('name', updatedName)
									.get()
		assertThatDocumentIsPresentInCollection(dbName, collectionName, expectedDocument)
	}
	
	@Test
	public void updatesDocumentInAPrefixedCollection() throws Exception {
		//Given
		def o2 = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
					.get()
					
		def o = new BasicDBObjectBuilder()
					.start()
						.add('name', updatedName)
					.get()
		
		def document = MongoUtils.updateDocument(dbName, prefixedCollectionName, o2, o) as DBObject
		
		//When
		operation.execute(document)

		//Then
		def expectedDocument = new BasicDBObjectBuilder()
									.start()
										.add('_id', objId)
										.add('name', updatedName)
									.get()
		assertThatDocumentIsPresentInCollection(dbName, prefixedCollectionName, expectedDocument)
	}
	
	@Test
	public void updatesDocumentInAnotherDatabase() throws Exception {
		//Given
		def o2 = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
					.get()
					
		def o = new BasicDBObjectBuilder()
					.start()
						.add('name', updatedName)
					.get()
		
		def document = MongoUtils.updateDocument(anotherDb, collectionName, o2, o) as DBObject

		//When
		operation.execute(document)

		//Then
		def expectedDocument = new BasicDBObjectBuilder()
									.start()
										.add('_id', objId)
										.add('name', updatedName)
									.get()
		assertThatDocumentIsPresentInCollection(anotherDb, collectionName, expectedDocument)
	}
	
	@Test
	public void updatesNestedDocument() {
		//Given
		def o2 = new BasicDBObjectBuilder()
					.start()
						.add('_id', nestedObjId)
					.get()
		
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', nestedObjId)
						.add('name', updatedName)
						.push('address')
							.add('street', '[Any Street]')
							.add('city', '[Any City]')
							.add('country', '[COUNT]')
						.pop()
					.get()
					
		def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as DBObject

		//When
		operation.execute(document)

		//Then
		def expectedDocument = new BasicDBObjectBuilder()
								.start()
									.add('_id', nestedObjId)
									.add('name', updatedName)
									.push('address')
										.add('street', '[Any Street]')
										.add('city', '[Any City]')
										.add('country', '[COUNT]')
									.pop()
								.get()
		assertThatDocumentIsPresentInCollection(dbName, collectionName, expectedDocument)
	}
	
	@Test
	public void shoutsWhenDocumentToUpdateDoesNotExistInTarget() throws Exception {
		//Given
		def objId = new ObjectId('509e8839f91e1d01ec6dfb50')
		def o2 = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
					.get()
					
		def o = new BasicDBObjectBuilder()
					.start()
						.add('name', updatedName)
					.get()
		
		def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as DBObject

		//When
		try {
			operation.execute(document)
			fail("Should not update document that does not exist")
		} catch (UpdateFailed problem) {
		  //Then
		  assertThat problem.message, is('Document does not exist { \"_id\" : { \"$oid\" : \"'+ objId +'\"}}')
		}
	}
}
