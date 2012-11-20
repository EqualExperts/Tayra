package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.fail

import org.bson.types.BSONTimestamp
import org.junit.Before
import org.junit.Test

import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

class DropDatabaseSpecs extends RequiresMongoConnection {
	def operation
	private String collectionName = 'home'

	@Before
	public void given() {
		operation = new DatabaseCommand(standalone)
	}
	
	@Test
	public void dropsDatabase() throws Exception {
		//Given
		givenADatabase()
		
		def oplogDocument = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : db + '.$cmd',
			o : new BasicDBObjectBuilder().start()
				.add('dropDatabase', 1)
				.get()
		)

		//When
		operation.execute(oplogDocument as DBObject)

		//Then
		List<String> databases = standalone.getDatabaseNames()
		assertThat databases, not(hasItem(db))
	}
	
	private void givenADatabase() {
		BasicDBObject dbObject = new BasicDBObject()
		dbObject.put("name", "test")
		standalone.getDB(db).createCollection(collectionName ,null)
		standalone.getDB(db).getCollection(collectionName).insert(dbObject)
	}
	
	@Test
	public void shoutsWhenDatabaseToBeDroppedDoesNotExist() throws Exception {
		//Given
		def oplogDocument = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : db + '.$cmd',
			o : new BasicDBObjectBuilder().start()
				.add('dropDatabase', 1)
				.get()
		)
		
		//When
		try {
			operation.execute(oplogDocument as DBObject)
			fail("Should not drop database that does not exist")
		} catch (DropDatabaseFailed problem) {
		  //Then
		  assertThat problem.message, is("Could Not Drop Database beaver")
		}
	}
}
