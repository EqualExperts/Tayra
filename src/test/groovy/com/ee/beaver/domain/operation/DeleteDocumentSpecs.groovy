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

class DeleteDocumentSpecs extends RequiresMongoConnection {
	private String collectionName = 'home'
	private String prefixedCollectionName = 'home.test'
	private String anotherDb = 'mongoose'
	def operation
	def objId = new ObjectId('509754dd2862862d511f6b57')
	def name = '[Test Name]'
	def documentToBeDeleted
	
	@Before
	public void givenDocumentsToDeleteExistInTargetDB() {
		documentToBeDeleted = new BasicDBObjectBuilder().start()
			.add('_id', objId)
			.add('name', name)
			.get()
			
		standalone.getDB(db).getCollection(collectionName).insert(documentToBeDeleted)
		standalone.getDB(db).getCollection(prefixedCollectionName).insert(documentToBeDeleted)
		standalone.getDB(anotherDb).getCollection(collectionName).insert(documentToBeDeleted)
		
		operation = new DeleteDocument(standalone)
	}
	
	@After
	public void cleanUp() {
		standalone.getDB(db).getCollection(collectionName).drop()
		standalone.getDB(db).getCollection(prefixedCollectionName).drop()
		standalone.getDB(anotherDb).dropDatabase()
	}
	
	private void assertThatDocumentIsNotPresentInCollection(String db, String collection, DBObject document) {
		assertThat standalone.getDB(db).getCollection(collection).findOne(document), is(null)
	}
	
	@Test
	public void deletesDocument() throws Exception {
		//Given
		def oplogDocument = new DeleteDocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'d',
			ns : "$db.$collectionName",
			b : true,
			o : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.get()
		)
		
		//When
		operation.execute(oplogDocument as DBObject)

		//Then
		assertThatDocumentIsNotPresentInCollection(db, collectionName, documentToBeDeleted)
	}

	@Test
	public void deletesDocumentInAPrefixedCollection() throws Exception {
		//Given
		def oplogDocument = new DeleteDocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'d',
			ns : "$db.$prefixedCollectionName",
			b : true,
			o : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.get()
		)		
		//When
		operation.execute(oplogDocument as DBObject)
		
		//Then
		assertThatDocumentIsNotPresentInCollection(db, prefixedCollectionName, documentToBeDeleted)
	}
	
	@Test
	public void deletesDocumentInAnotherDatabase() throws Exception {
		//Given
		def oplogDocument = new DeleteDocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'d',
			ns : "$anotherDb.$collectionName",
			b : true,
			o : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.add('name', name)
				.get()
		)

		//When
		operation.execute(oplogDocument as DBObject)

		//Then
		assertThatDocumentIsNotPresentInCollection(anotherDb, collectionName, documentToBeDeleted)
	}
	
	@Test
	public void deletesNestedDocument() {
		//Given
		def objId = new ObjectId('5097791244ae3e2f63ec6d32')
		def documentToBeDeleted = new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.add('name', name)
				.push('address')
					.add('street', '[Some Street]')
					.add('city', '[Some City]')
					.add('country', '[CN]')
				.pop()
				.get()
				
		standalone.getDB(db).getCollection(collectionName).insert(documentToBeDeleted)
		
		def oplogDocument = new DeleteDocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'d',
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
		assertThatDocumentIsNotPresentInCollection(db, collectionName, documentToBeDeleted)
	}
}
