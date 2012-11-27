package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.fail

import org.junit.After
import org.junit.Before
import org.junit.Test

import com.mongodb.CommandResult
import com.mongodb.DB
import com.mongodb.DBObject

class CreateCollectionSpecs extends RequiresMongoConnection {
	
	def operation
	DB database
	private String collectionName = 'home'
	private MongoUtils mongoUtils = new MongoUtils()
	
	@Before
	public void givenADatabase() {
		operation = new CreateCollection()
		database = standalone.getDB(dbName)
	}
	
	@After
	public void dropDB() {
		standalone.getDB(dbName).getCollection(collectionName).drop()
	}

	@Test
	public void createsACollection() throws Exception {
		//Given
		def builder = mongoUtils.createCollection(dbName, collectionName)
		DBObject spec = builder.o

		//When
		operation.execute(database, spec)
		
		//Then
		def collectionExists = standalone.getDB(dbName).collectionExists(collectionName)
		assertThat collectionExists, is(true)    
	}
	
	@Test
	public void shoutsWhenACollectionAlreadyExists() {
		//Given
		def builder = mongoUtils.createCollection(dbName, collectionName)
		DBObject spec = builder.o
		
		//When
		operation.execute(database, spec)
		try {
			operation.execute(database, spec)
		    fail("Should not have created collection: $collectionName, as it already exists!")
		} catch (CreateCollectionFailed problem) {
		  assertThat problem.message, is("command failed [command failed [create] { \"serverUsed\" : \"localhost:27020\" , \"errmsg\" : \"collection already exists\" , \"ok\" : 0.0}")
		}
	}
	
	@Test
	public void createsACappedCollection() throws Exception {
		//Given
		def builder = mongoUtils.createCollection(dbName, collectionName,true,2048,1024)
		DBObject spec = builder.o

		//When
		operation.execute(database, spec)
		
		//Then
		DB db = standalone.getDB(dbName)
		assertThat db.collectionExists(collectionName), is(true)
		
		CommandResult result = db.getCollection(collectionName).getStats()
		assertThat result.get('capped'), is(true)
		assertThat result.get('max'), is(1024)
	}
	
	@Test
	public void createsCollectionWithSize() {
		//Given
		def builder = mongoUtils.createCollection(dbName, collectionName,false,2048,null)
		DBObject spec = builder.o
		
		//When
		operation.execute(database, spec)
		
		//Then
		def collectionExists = standalone.getDB(dbName).collectionExists(collectionName)
		assertThat collectionExists, is(true)
	}
}
