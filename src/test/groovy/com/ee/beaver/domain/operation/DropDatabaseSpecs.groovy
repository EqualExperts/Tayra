package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.fail

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
		operation = new DatabaseCommand(standalone)
	}
	
	@Test
	public void dropsDatabase() throws Exception {
		//Given
		String dbName = 'databaseToBeDropped'
		givenADatabase(dbName, 'home')
		
		def oplogDocument = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : dbName + '.$cmd',
			o : new BasicDBObjectBuilder().start()
				.add('dropDatabase', 1)
				.get()
		)

		//When
		operation.execute(oplogDocument as DBObject)

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
		def oplogDocument = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : nonExistentDB + '.$cmd',
			o : new BasicDBObjectBuilder().start()
				.add('dropDatabase', 1)
				.get()
		)
		
		//When
		try {
			operation.execute(oplogDocument as DBObject)
			fail('Should not drop database that does not exist')
		} catch (DropDatabaseFailed problem) {
		  //Then
		  assertThat problem.message, containsString("Could Not Drop Database $nonExistentDB")
		}
	}
}
