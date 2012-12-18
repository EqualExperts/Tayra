package com.ee.beaver.domain.operation

import org.bson.types.ObjectId

import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

class UpdateDocumentSpecs extends RequiresMongoConnection {
	
	private String collectionName = 'home'
	private String prefixedCollectionName = 'home.test'
	private String anotherDb = 'mongoose'
	private List categories;
	def operation
	def objId = new ObjectId('509754dd2862862d511f6b57')
	def nestedObjId = new ObjectId('509754dd2862862d511f6b58')
	def name = '[Test Name]'
	def updatedName = '[Test Name 2]'
	def documentToBeUpdated
	def nestedDocumentToBeUpdated
	def setup() {
		documentToBeUpdated = new BasicDBObjectBuilder()
								.start()
									.add('_id', objId)
									.add('name', name)
									.add('city', 'test city')
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
	
	def cleanup() {
		standalone.getDB(dbName).getCollection(collectionName).drop()
		standalone.getDB(dbName).getCollection(prefixedCollectionName).drop()
		standalone.getDB(anotherDb).dropDatabase()
	}
	
	def assertThatDocumentIsPresentInCollection(String db, String collection, DBObject document) {
		standalone.getDB(db).getCollection(collection).findOne(document) == document
	}
	
	
	def updatesDocument() throws Exception {
		given: 'an oplog entry for update'
			def o2 = new BasicDBObjectBuilder()
						.start()
							.add('_id', objId)
						.get()
						
			def o = new BasicDBObjectBuilder()
						.start()
							.add('name', updatedName)
						.get()
			
			def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as String
			
		when: 'the operation runs'
			operation.execute(document)

		then: 'updated document should exist'
			def expectedDocument = new BasicDBObjectBuilder()
										.start()
											.add('_id', objId)
											.add('name', updatedName)
										.get()
			assertThatDocumentIsPresentInCollection(dbName, collectionName, expectedDocument)
	}
	
	
	def updatesDocumentInAPrefixedCollection() throws Exception {
		given: 'an update document oplog entry on a prefixed collection'
			def o2 = new BasicDBObjectBuilder()
						.start()
							.add('_id', objId)
						.get()
						
			def o = new BasicDBObjectBuilder()
						.start()
							.add('name', updatedName)
						.get()
			
			def document = MongoUtils.updateDocument(dbName, prefixedCollectionName, o2, o) as String
		
		when: 'the operation runs'
			operation.execute(document)

		then: 'updated document should exist'
			def expectedDocument = new BasicDBObjectBuilder()
										.start()
											.add('_id', objId)
											.add('name', updatedName)
										.get()
			assertThatDocumentIsPresentInCollection(dbName, prefixedCollectionName, expectedDocument)
	}
	
	
	def updatesDocumentInAnotherDatabase() throws Exception {
		given: 'an update document oplog entry on another database'
			def o2 = new BasicDBObjectBuilder()
						.start()
							.add('_id', objId)
						.get()
						
			def o = new BasicDBObjectBuilder()
						.start()
							.add('name', updatedName)
						.get()
			
			def document = MongoUtils.updateDocument(anotherDb, collectionName, o2, o) as String

		when: 'the operation runs'
			operation.execute(document)

		then: 'updated document should exist'
			def expectedDocument = new BasicDBObjectBuilder()
										.start()
											.add('_id', objId)
											.add('name', updatedName)
										.get()
			assertThatDocumentIsPresentInCollection(anotherDb, collectionName, expectedDocument)
	}
	
	
	def updatesNestedDocument() {
		given: 'an update document oplog entry for a nested document'
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
						
			def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as String

		when: 'the operation runs'
			operation.execute(document)

		then: 'updated document should exist'
			def expectedDocument = new BasicDBObjectBuilder()
									.start()
										.add('_id', nestedObjId)
										.push('address')
											.add('street', '[Any Street]')
											.add('city', '[Any City]')
											.add('country', '[COUNT]')
										.pop()
										.add('name', updatedName)
									.get()
			assertThatDocumentIsPresentInCollection(dbName, collectionName, expectedDocument)
	}
	
	
	def shoutsWhenDocumentToUpdateDoesNotExistInTarget() throws Exception {
		given: 'an update document oplog entry for a non-existing document'
			def objId = new ObjectId('509e8839f91e1d01ec6dfb50')
			def o2 = new BasicDBObjectBuilder()
						.start()
							.add('_id', objId)
						.get()
						
			def o = new BasicDBObjectBuilder()
						.start()
							.add('name', updatedName)
						.get()
			
			def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as String

		when: 'the operation runs'
			operation.execute(document)
			
		then: 'it complains that document to be updated does not exist'
			def problem = thrown(UpdateFailed)
			problem.message == 'Document does not exist { \"_id\" : { \"$oid\" : \"'+ objId +'\"}}'
	}
	
}
