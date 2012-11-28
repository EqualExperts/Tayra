package com.ee.beaver.domain.operation

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBObject

class DropDatabaseSpecs extends RequiresMongoConnection {
	
	def operation
	
	def setup() {
		operation = new DropDatabase(standalone)
	}
	
	def dropsDatabase() throws Exception {
		given: 'a database exists'
			String dbName = 'databaseToBeDropped'
			givenADatabase(dbName, 'home')
		
		and: 'a drop database oplog entry payload'
			def builder = MongoUtils.dropDatabase(dbName)
			DBObject spec = builder.o

		when: 'the operation runs'
			operation.execute(standalone.getDB(dbName), spec)

		then: 'the database should not exist'
			List<String> databases = standalone.getDatabaseNames()
			!databases.contains(dbName)
	}
	
	private void givenADatabase(dbName, collectionName) {
		DB createDB = standalone.getDB(dbName)
		DBCollection collection = createDB.createCollection(collectionName ,null)
		collection.insert(new BasicDBObjectBuilder().start().get())
	}
	
	
	def shoutsWhenDatabaseToBeDroppedDoesNotExist() throws Exception {
		given: 'a drop database oplog entry payload for a non-existent database'
			def nonExistentDB = 'nonExistentDB'
			
			def builder = MongoUtils.dropDatabase(nonExistentDB)
			DBObject spec = builder.o
		
		when: 'the operation runs'
			operation.execute(standalone.getDB(nonExistentDB), spec)
			
		then: 'it complains that database to be dropped does not exist'
			def problem = thrown(DropDatabaseFailed)
			 problem.message == "Could Not Drop Database $nonExistentDB"
	}
}
