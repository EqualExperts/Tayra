package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.fail

import org.bson.types.ObjectId
import org.junit.After
import org.junit.Before
import org.junit.Test

import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

class DeleteDocumentSpecs extends RequiresMongoConnection {
	
	private String collectionName = 'home'
	private String prefixedCollectionName = 'home.test'
	private String anotherDb = 'mongoose'
	private MongoUtils mongoUtils = new MongoUtils()
	def operation
	def objId = new ObjectId('509754dd2862862d511f6b57')
	def name = '[Test Name]'
	def documentToBeDeleted
	
	@Before
	public void givenDocumentsToDeleteExistInTargetDB() {
		documentToBeDeleted = new BasicDBObjectBuilder()
								.start()
									.add('_id', objId)
									.add('name', name)
								.get()
		
		insertIn(dbName, collectionName, documentToBeDeleted)
		insertIn(dbName, prefixedCollectionName, documentToBeDeleted)
		insertIn(anotherDb, collectionName, documentToBeDeleted)	
		operation = new DeleteDocument(standalone)
	}
	
	private insertIn (String db, String collectionName, BasicDBObject documentToBeDeleted) {
		standalone.getDB(db).getCollection(collectionName).insert(documentToBeDeleted)
	}
	
	@After
	public void cleanUp() {
		standalone.getDB(dbName).getCollection(collectionName).drop()
		standalone.getDB(dbName).getCollection(prefixedCollectionName).drop()
		standalone.getDB(anotherDb).dropDatabase()
	}
	
	private void assertThatDocumentIsNotPresentInCollection(String db, String collection, DBObject document) {
		assertThat standalone.getDB(db).getCollection(collection).findOne(document), is(null)
	}
	
	@Test
	public void deletesDocument() throws Exception {
		//Given
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
					.get()
		def document = mongoUtils.deleteDocument(dbName, collectionName,o) as DBObject
		
		//When
		operation.execute(document)

		//Then
		assertThatDocumentIsNotPresentInCollection(dbName, collectionName, documentToBeDeleted)
	}

	@Test
	public void deletesDocumentInAPrefixedCollection() throws Exception {
		//Given
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
					.get()
		def document = mongoUtils.deleteDocument(dbName, prefixedCollectionName,o) as DBObject
		
		//When
		operation.execute(document)
		
		//Then
		assertThatDocumentIsNotPresentInCollection(dbName, prefixedCollectionName, documentToBeDeleted)
	}
	
	@Test
	public void deletesDocumentInAnotherDatabase() throws Exception {
		//Given
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
					.get()
		def document = mongoUtils.deleteDocument(anotherDb, collectionName,o) as DBObject
		
		//When
		operation.execute(document)

		//Then
		assertThatDocumentIsNotPresentInCollection(anotherDb, collectionName, documentToBeDeleted)
	}
	
	@Test
	public void deletesNestedDocument() {
		//Given
		def objId = new ObjectId('5097791244ae3e2f63ec6d32')
		def documentToBeDeleted = new BasicDBObjectBuilder()
									.start()
										.add('_id', objId)
										.add('name', name)
										.push('address')
											.add('street', '[Some Street]')
											.add('city', '[Some City]')
											.add('country', '[CN]')
										.pop()
									.get()
				
		standalone.getDB(dbName).getCollection(collectionName).insert(documentToBeDeleted)
		
		def document = mongoUtils.deleteDocument(dbName, collectionName, documentToBeDeleted) as DBObject

		//When
		operation.execute(document)

		//Then
		assertThatDocumentIsNotPresentInCollection(dbName, collectionName, documentToBeDeleted)
	}
	
	@Test
	public void shoutsWhenDocumentToDeleteDoesNotExistInTarget() throws Exception {
		//Given
		def absentObjId = new ObjectId('509e8839f91e1d01ec6dfb50')
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', absentObjId)
					.get()
		def document = mongoUtils.deleteDocument(dbName, collectionName,o) as DBObject
		
		//When
		try {
			operation.execute(document)
			fail("Should not delete document that does not exist")
		} catch (DeleteFailed problem) {
		  //Then
		  assertThat problem.message, is('Document does not exist { \"_id\" : { \"$oid\" : \"'+ absentObjId +'\"}}')
		}
	}
}
