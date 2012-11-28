package com.ee.beaver.domain.operation

import com.mongodb.CommandResult
import com.mongodb.DB
import com.mongodb.DBObject
import spock.lang.*

class CreateCollectionSpecs extends RequiresMongoConnection {
	
	def operation
	DB database
	private String collectionName = 'home'
	
	def setup() {
		operation = new CreateCollection()
		database = standalone.getDB(dbName)
	}
	
	def cleanup() {
		standalone.getDB(dbName).getCollection(collectionName).drop()
	}

	def createsACollection() throws Exception {
		given: 'a create collection oplog entry'
			def builder = MongoUtils.createCollection(dbName, collectionName)
			DBObject spec = builder.o

		when: 'the operation runs'
			operation.execute(database, spec)
		
		then: 'collection should exist'
			standalone.getDB(dbName).collectionExists(collectionName)
	}
	
	
	def shoutsWhenACollectionAlreadyExists() {
		given: 'a create collection oplog entry'
			def builder = MongoUtils.createCollection(dbName, collectionName)
			DBObject spec = builder.o
		
		and: 'the collection already exists'
			operation.execute(database, spec)
		
		when: 'the operation runs again'
			operation.execute(database, spec)

		then: 'it complains that the collection cannot be created again'
			def problem = thrown(CreateCollectionFailed)
		    problem.message == "command failed [command failed [create] { \"serverUsed\" : \"localhost:27020\" , \"errmsg\" : \"collection already exists\" , \"ok\" : 0.0}"
	}
	
	
	def createsACappedCollection() throws Exception {
		given: 'a create capped collection oplog entry'
			def isCapped = true
			def ignoreMaxSize = 1024
			def builder = MongoUtils.createCollection(dbName, collectionName, isCapped, 2048, ignoreMaxSize)
			DBObject spec = builder.o

		when: 'the operation runs'
			operation.execute(database, spec)
		
		then: 'the capped collection with size should exist'
			DB db = standalone.getDB(dbName)
			db.collectionExists(collectionName)
		
			CommandResult result = db.getCollection(collectionName).getStats()
			result.get('capped')
			result.get('max')== 1024
	}
	
	def createsCollectionWithSize() {
		given:'a create collection oplog entry with specific size'
		    def isCapped = false
			def ignoreMaxSize = null
			def builder = MongoUtils.createCollection(dbName, collectionName, isCapped, 2048, ignoreMaxSize)
			DBObject spec = builder.o
		
		when: 'the operation runs'
			operation.execute(database, spec)
		
		then: 'the collection should exist'
			standalone.getDB(dbName).collectionExists(collectionName)
	}
}
