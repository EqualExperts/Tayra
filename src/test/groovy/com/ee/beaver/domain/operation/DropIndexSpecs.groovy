package com.ee.beaver.domain.operation

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

class DropIndexSpecs extends RequiresMongoConnection{
	
	def collectionName = 'toys'	
	def operation
	def setup() {		
		operation = new DropIndex()
	}
	
	def assertThatIndexIsNotPresent(spec) {
		def dropped = true
		List<DBObject> indices = standalone.getDB(dbName).getCollection(collectionName).getIndexInfo()
			for ( DBObject index : indices) {
				if (index.get("name") == spec.get("index")){
					dropped = false;
				}
			}
		return dropped
	}
	
	def dropsASimpleIndex() {
		def key = new BasicDBObjectBuilder()
					.start()
						.add('roll', 1)
					.get()
		standalone.getDB(dbName).getCollection(collectionName).ensureIndex(key)
		
		given: 'drop index oplog entry'
			DBObject spec = new BasicDBObjectBuilder()
						.start()
							.add('deleteIndexes', collectionName)
							.add('index', 'roll_1')
						.get()
						
		when: 'Operation runs'
			operation.doExecute(standalone.getDB(dbName), spec)
		
		then: 'Index should not be present'
			assertThatIndexIsNotPresent(spec)
	}
	
	def dropsACompoundIndex() {
		def key = new BasicDBObjectBuilder()
					.start()
						.add('roll', 1)
						.add('name', 1)
					.get()
		standalone.getDB(dbName).getCollection(collectionName).ensureIndex(key)
		
		given: 'drop index oplog entry for a compound index'
			DBObject spec = new BasicDBObjectBuilder()
						.start()
							.add('deleteIndexes', collectionName)
							.add('index', 'roll_1_name_1')						
						.get()
						
		when: 'Operation runs'
			operation.doExecute(standalone.getDB(dbName), spec)
			
		then: 'Index should not be present'
			assertThatIndexIsNotPresent(spec)
	}
	
	def dropsAnIndexOnNestedField() {
		def key = new BasicDBObjectBuilder()
					.start()
						.add('name.fname', 1)						
					.get()
		standalone.getDB(dbName).getCollection(collectionName).ensureIndex(key)
		
		given: 'drop index oplog entry for a compound index'
			DBObject spec = new BasicDBObjectBuilder()
						.start()
							.add('deleteIndexes', collectionName)
							.add('index', 'name.fname_1')
						.get()
						
		when: 'Operation runs'
			operation.doExecute(standalone.getDB(dbName), spec)
			
		then: 'Index should not be present'
			assertThatIndexIsNotPresent(spec)
	}
	
	
	def shoutsWhenIndexToBeDroppedDoesNotExist() throws Exception {
		given: 'a drop index oplog entry payload for a non-existent index'
			String indexName = 'roll_1'
			DBObject spec = new BasicDBObjectBuilder()
						.start()
							.add('deleteIndexes', collectionName)
							.add('index', indexName)
						.get()
		
		when: 'the operation runs'
			operation.doExecute(standalone.getDB(dbName), spec)
			
		then: 'it complains that index to be dropped does not exist'
			def problem = thrown(DropIndexFailed)
			problem.message == "Cannot drop index : " + indexName + " Index doesn't exist."
	}

}
