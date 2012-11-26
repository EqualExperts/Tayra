package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.fail

import java.sql.DatabaseMetaData;

import org.bson.types.BSONTimestamp
import org.junit.Before
import org.junit.Test

import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBObject

class DropDatabaseSpecs extends RequiresMongoConnection {
	def operation

	@Before
	public void given() {
		operation = new DropDatabase(standalone)
	}
	
	@Test
	public void dropsDatabase() throws Exception {
		//Given
		String dbName = 'databaseToBeDropped'
		givenADatabase(dbName, 'home')
		
		DBObject spec = new BasicDBObjectBuilder().start()
							.add('dropDatabase', 1)
							.get()

		//When
		operation.execute(standalone.getDB(dbName), spec)

		//Then
		List<String> databases = standalone.getDatabaseNames()
		assertThat databases, not(hasItem(dbName))
	}
	
	private void givenADatabase(dbName, collectionName) {
		DB createDB = standalone.getDB(dbName)
		DBCollection collection = createDB.createCollection(collectionName ,null)
		collection.insert(new BasicDBObjectBuilder().start().get())
	}
	
	@Test
	public void shoutsWhenDatabaseToBeDroppedDoesNotExist() throws Exception {
		//Given
		def nonExistentDB = 'nonExistentDB'
		
		DBObject spec = new BasicDBObjectBuilder().start()
							.add('dropDatabase', 1)
							.get()
		
		//When
		try {
			operation.execute(standalone.getDB(nonExistentDB), spec)
			fail('Should not drop database that does not exist')
		} catch (DropDatabaseFailed problem) {
		  //Then
		  assertThat problem.message, containsString("Could Not Drop Database $nonExistentDB")
		}
	}
}
