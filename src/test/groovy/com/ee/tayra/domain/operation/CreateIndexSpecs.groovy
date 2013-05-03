package com.ee.tayra.domain.operation

import com.ee.tayra.domain.operation.InsertDocument;
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import org.bson.types.ObjectId

class CreateIndexSpecs extends RequiresMongoConnection {
	
	def collectionName = 'toys'
	def operation

	def cleanup() {
		standalone.getDB(dbName).getCollection(collectionName).dropIndexes()
	}
	
	private boolean assertThatIndexIsPresentOnCollection(String db, String collection, DBObject document) {
		def created = false
		List<DBObject> indices = standalone.getDB(dbName).getCollection(collectionName).getIndexInfo()
			for ( DBObject index : indices) {
				if (index.get("name") == document.get("name")){
					created = true;
				}
			}
		return created	
	}
	
	def createsSimpleIndex() {
		given: 'an index creation oplog entry'
			def o = new BasicDBObjectBuilder()
						.start()
							.add('_id', new ObjectId('50c57350840e4480d8a636fd'))
							.add('ns', dbName + "." + collectionName )
							.push('key')
								.add('roll', 1)
							.pop()
							.add('name', 'roll_1')
						.get()
			def indexDocument = MongoUtils.insertDocument(dbName, "system.indexes", o) as DBObject
			
		and: 'an insert document operation to create index'
			operation = new InsertDocument(standalone)

		when: 'the operation runs'
			operation.execute(indexDocument.toString())
		
		then: 'index should be created'
			assertThatIndexIsPresentOnCollection(dbName, collectionName, o)
	}
	
	def createsCompoundIndex() {
		given: 'a compound index creation oplog entry'
			def o = new BasicDBObjectBuilder()
						.start()
							.add('_id', new ObjectId('50c57350840e4480d8a636fd'))
							.add('ns', dbName + "." + collectionName )
							.push('key')
								.add('name', 1)
								.add('age', 1)
							.pop()
							.add('name', 'name_1_age_1')
						.get()
			def indexDocument = MongoUtils.insertDocument(dbName, "system.indexes", o) as DBObject

		and: 'an insert document operation to create index'
			operation = new InsertDocument(standalone)
			
		when: 'the operation runs'
			operation.execute(indexDocument.toString())
		
		then: 'index should be created'
			assertThatIndexIsPresentOnCollection(dbName, collectionName, o)
	}
	
	def createsIndexForNestedDocuments() {
		given: 'an index creation oplog entry for a nested document field'
			def o = new BasicDBObjectBuilder()
						.start()
							.add('_id', new ObjectId('50c57350840e4480d8a636fd'))
							.add('ns', dbName + "." + collectionName )
							.push('key')
								.add('name.fname', 1)
							.pop()
							.add('name', 'name.fname_1')
						.get()
			def indexDocument = MongoUtils.insertDocument(dbName, "system.indexes", o) as DBObject
			
		and: 'an insert document operation to create index'
			operation = new InsertDocument(standalone)
			
		when: 'the operation runs'
			operation.execute(indexDocument.toString())
		
		then: 'index should be created'
			assertThatIndexIsPresentOnCollection(dbName, collectionName, o)
	}
	
	def createsIndexWithUniqueOption() {
		given: 'an index creation oplog entry with unique option'
			def o = new BasicDBObjectBuilder()
						.start()
							.add('_id', new ObjectId('50c57350840e4480d8a636fd'))
							.add('ns', dbName + "." + collectionName )
							.push('key')
								.add('roll', 1)
							.pop()
							.add('name', 'roll_1')
							.add('unique', true)
						.get()
			def indexDocument = MongoUtils.insertDocument(dbName, "system.indexes", o) as DBObject

		and: 'an insert document operation to create index'
			operation = new InsertDocument(standalone)
			
		when: 'the operation runs'
			operation.execute(indexDocument.toString())
		
		then: 'index should be created'
			assertThatIndexIsPresentOnCollection(dbName, collectionName, o)
		
	}
	
	def createsIndexWithSparseOption() {
		given: 'an index creation oplog entry with sparse option'
			def o = new BasicDBObjectBuilder()
						.start()
							.add('_id', new ObjectId('50c57350840e4480d8a636fd'))
							.add('ns', dbName + "." + collectionName )
							.push('key')
								.add('roll', 1)
							.pop()
							.add('name', 'roll_1')
							.add('sparse', true)
						.get()
			def indexDocument = MongoUtils.insertDocument(dbName, "system.indexes", o) as DBObject

		and: 'an insert document operation to create index'
			operation = new InsertDocument(standalone)
			
		when: 'the operation runs'
			operation.execute(indexDocument.toString())
		
		then: 'index should be created'
			assertThatIndexIsPresentOnCollection(dbName, collectionName, o)
	}
	
	def createsIndexWithUniqueAndSparseOption() {
		given: 'an index creation oplog entry with unique and sparse option'
			def o = new BasicDBObjectBuilder()
						.start()
							.add('_id', new ObjectId('50c57350840e4480d8a636fd'))
							.add('ns', dbName + "." + collectionName )
							.push('key')
								.add('roll', 1)
							.pop()
							.add('name', 'roll_1')
							.add('unique', true)
							.add('sparse', true)
						.get()
			def indexDocument = MongoUtils.insertDocument(dbName, "system.indexes", o) as DBObject

		and: 'an insert document operation to create index'
			operation = new InsertDocument(standalone)
			
		when: 'the operation runs'
			operation.execute(indexDocument.toString())
		
		then: 'index should be created'
			assertThatIndexIsPresentOnCollection(dbName, collectionName, o)
	}
	
	def createsIndexWithUniqueAndDropDupsOption() {
		given: 'an index creation oplog entry with unique option'
			def o = new BasicDBObjectBuilder()
						.start()
							.add('_id', new ObjectId('50c57350840e4480d8a636fd'))
							.add('ns', dbName + "." + collectionName )
							.push('key')
								.add('roll', 1)
							.pop()
							.add('name', 'roll_1')
							.add('unique', true)
							.add('dropDups', true)
						.get()
			def indexDocument = MongoUtils.insertDocument(dbName, "system.indexes", o) as DBObject

		and: 'an insert document operation to create index'
			operation = new InsertDocument(standalone)
			
		when: 'the operation runs'
			operation.execute(indexDocument.toString())
		
		then: 'index should be created'
			assertThatIndexIsPresentOnCollection(dbName, collectionName, o)
	}
}
