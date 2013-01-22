package com.ee.tayra.domain.operation

import org.bson.types.ObjectId

import com.ee.tayra.domain.operation.UpdateDocument;
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

class UpdateOperatorSpecs extends RequiresMongoConnection {
	
	private String collectionName = 'home'
	private List categories;
	def operation
	def objId = new ObjectId('509754dd2862862d511f6b57')
	def nestedObjId = new ObjectId('509754dd2862862d511f6b58')
	def arrayObjId = new ObjectId('509754dd2862862d511f6b60')
	def name = '[Test Name]'
	def updatedName = '[Test Name 2]'
	def documentToBeUpdated
	def nestedDocumentToBeUpdated
	def arrayDocumentToBeUpdated
	def categories
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
									
		categories = new ArrayList();
		categories.add('computers')
		categories.add('mobiles')
		arrayDocumentToBeUpdated = new BasicDBObjectBuilder()
									.start()
										.add('_id', arrayObjId)
										.add('name', name)
										.add('categories', categories)
									.get()							
		insertIn(dbName, collectionName, documentToBeUpdated)
		insertIn(dbName, collectionName, nestedDocumentToBeUpdated)
		insertIn(dbName, collectionName, arrayDocumentToBeUpdated)
		operation = new UpdateDocument(standalone)
	}
	
	private void insertIn(String db, String collectionName, BasicDBObject documentToBeUpdated) {
		standalone.getDB(db).getCollection(collectionName).insert(documentToBeUpdated)
	}
	
	def cleanup() {
		standalone.getDB(dbName).getCollection(collectionName).drop()
	}
	
	def assertThatDocumentIsPresentInCollection(String db, String collection, DBObject document) {
		standalone.getDB(db).getCollection(collection).findOne(document) == document
	}
	
	
	def updatesDocument() throws Exception {
		given: 'an oplog entry for update, upsert'
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

	def updatesDocumentWithSetOperator() throws Exception {
		given: 'an update document oplog entry for $set, $inc'
			def o2 = new BasicDBObjectBuilder()
						.start()
							.add('_id', objId)							
						.get()
						
			def o = new BasicDBObjectBuilder()
						.start()
							.push('$set')
								.add('state', 'test state')
							.pop()
						.get()
			
			def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as String
			
		when: 'the operation runs'
			operation.execute(document)

		then: 'updated document should exist'
			def expectedDocument = new BasicDBObjectBuilder()
										.start()
											.add('_id', objId)
											.add('name', name)
											.add('city', 'test city')
											.add('state' , 'test state')
										.get()
			assertThatDocumentIsPresentInCollection(dbName, collectionName, expectedDocument)
	}
	
	def updatesDocumentWithUnSetOperator() throws Exception {
		given: 'an update document oplog entry for $unset'
			def o2 = new BasicDBObjectBuilder()
						.start()
							.add('_id', objId)
						.get()
						
			def o = new BasicDBObjectBuilder()
						.start()
							.push('$unset')
								.add('city', '')
							.pop()
						.get()
			
			def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as String
			
		when: 'the operation runs'
			operation.execute(document)

		then: 'updated document should exist'
			def expectedDocument = new BasicDBObjectBuilder()
										.start()
											.add('_id', objId)
											.add('name', name)											
										.get()
			assertThatDocumentIsPresentInCollection(dbName, collectionName, expectedDocument)
	}
	
	def updatesNestedDocumentWithSetOperator() throws Exception {
		given: 'an update document oplog entry for $set operation on a nested document'
			def o2 = new BasicDBObjectBuilder()
						.start()
							.add('_id', nestedObjId)
						.get()
						
			def o = new BasicDBObjectBuilder()
						.start()
							.push('$set')
								.add('address.state', 'test state')
							.pop()
						.get()
			
			def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as String
			
		when: 'the operation runs'
			operation.execute(document)

		then: 'updated document should exist'
			def expectedDocument = new BasicDBObjectBuilder()
										.start()
											.add('_id', nestedObjId)
											.add('name', name)
											.push('address')
												.add('city', '[Some City]')
												.add('country', '[CN]')
												.add('state' , 'test state')
												.add('street', '[Some Street]')
											.pop()
										.get()
			assertThatDocumentIsPresentInCollection(dbName, collectionName, expectedDocument)
	}
	
	def updatesArrayFieldWithAddToSetOperator() {
		given: 'an update document oplog entry for $addToSet, $push'
			def o2 = new BasicDBObjectBuilder()
						.start()
							.add('_id', arrayObjId)
						.get()
						
			def o = new BasicDBObjectBuilder()
						.start()
							.push('$set')
								.add('categories.2', 'test category')
							.pop()
						.get()
			
			def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as String
			
		when: 'the operation runs'
			operation.execute(document)
		
		then: 'updated document should exist'
			categories.add('test category')
			def expectedDocument = new BasicDBObjectBuilder()
									.start()
									   .add('_id', arrayObjId)
									   .add('name', name)
									   .add('categories', categories)
								   .get()
			assertThatDocumentIsPresentInCollection(dbName, collectionName, expectedDocument)
	}
	
	def updatesArrayFieldWithAddToSetForEachOperator() {
		given: 'an update document oplog entry for $addToSet with $each, $pushAll, $pullAll, $pop, $pull'
			def o2 = new BasicDBObjectBuilder()
						.start()
							.add('_id', arrayObjId)
						.get()
			List categoryList = new ArrayList()
			categoryList.addAll(categories)	
			categoryList.add('test category 1')
			categoryList.add('test category 2')
			def o = new BasicDBObjectBuilder()
						.start()
							.push('$set')
								.add('categories', categoryList)
							.pop()
						.get()
			
			def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as String
			
		when: 'the operation runs'
			operation.execute(document)
		
		then: 'updated document should exist'
			categories.add('test category 1')
			categories.add('test category 2')
			def expectedDocument = new BasicDBObjectBuilder()
									.start()
									   .add('_id', arrayObjId)
									   .add('name', name)
									   .add('categories', categories)
								   .get()
			assertThatDocumentIsPresentInCollection(dbName, collectionName, expectedDocument)
	}
	
	def updatesDocumentWithRenameOperatorForSingleField() throws Exception {
		given: 'an update document oplog entry for $rename'
			def o2 = new BasicDBObjectBuilder()
						.start()
							.add('_id', objId)
						.get()
						
			def o = new BasicDBObjectBuilder()
						.start()
							.push('$unset')
								.add('city', 1)
							.pop()
							.push('$set')
								.add('place', 'test city')
							.pop()							
						.get()
			
			def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as String
			
		when: 'the operation runs'
			operation.execute(document)

		then: 'updated document should exist'
			def expectedDocument = new BasicDBObjectBuilder()
										.start()
											.add('_id', objId)
											.add('name', name)
											.add('place', 'test city')
										.get()
			assertThatDocumentIsPresentInCollection(dbName, collectionName, expectedDocument)
	}
}
