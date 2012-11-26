package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.fail

import org.junit.After
import org.junit.Before
import org.junit.Test

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.CommandResult
import com.mongodb.DB
import com.mongodb.DBObject
import com.mongodb.Mongo

class CreateCollectionSpecs extends RequiresMongoConnection {
	def operation
	DB database
	private String collectionName = 'home'
	
	@Before
	public void givenADatabase() {
		operation = new CreateCollection()
		database = standalone.getDB(db)
	}
	
	@After
	public void dropDB() {
		standalone.getDB(db).getCollection(collectionName).drop()
	}

	@Test
	public void createsACollection() throws Exception {
		//Given
		DBObject spec = new BasicDBObjectBuilder()
							.start()
								.add('create', collectionName)
								.add('capped', false)
								.add('size', null)
								.add('max', null)
							.get()
							
		//When
		operation.execute(database, spec)
		
		//Then
		def collectionExists = standalone.getDB(db).collectionExists(collectionName)
		assertThat collectionExists, is(true)    
	}
	
	@Test
	public void shoutsWhenACollectionAlreadyExists() {
		//Given
		DBObject spec = new BasicDBObjectBuilder().start()
							.add('create', collectionName)
							.add('capped', false)
							.add('size', null)
							.add('max', null)
							.get()
		
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
		DBObject spec = new BasicDBObjectBuilder().start()
							.add('create', collectionName)
							.add('capped', true)
							.add('size', 65536)
							.add('max', 2048)
						.get()
							
		//When
		operation.execute(database, spec)
		
		//Then
		DB db = standalone.getDB(db)
		assertThat db.collectionExists(collectionName), is(true)
		
		CommandResult result = db.getCollection(collectionName).getStats()
		assertThat result.get('capped'), is(true)    
		assertThat result.get('max'), is(2048)    
	}
	//TODO: spec for collection with size
	
}
