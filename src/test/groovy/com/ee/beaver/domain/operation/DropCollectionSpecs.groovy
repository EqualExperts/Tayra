package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.fail

import org.junit.Before
import org.junit.Test

import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DB
import com.mongodb.DBObject
import com.mongodb.Mongo

class DropCollectionSpecs extends RequiresMongoConnection {
	
	def operation
	private String collectionName = 'home'
	private String cappedCollectionName = 'person'
	private String absentCollectionName = 'people'
	private DB db
	private MongoUtils mongoUtils = new MongoUtils()
	
	@Before
	public void givenADb() {
		operation = new DropCollection()
		db = standalone.getDB(dbName)
	}
	
	@Test
	public void dropsACollection() throws Exception {
		//Given
		givenACollection()
		
		def builder = mongoUtils.dropCollection(dbName, collectionName)
		DBObject spec = builder.o

		//When
		operation.execute(db, spec)
		
		//Then
		def collectionExists = db.collectionExists(collectionName)
		assertThat collectionExists, is(false)
	}

	private givenACollection() {
		BasicDBObject dbobj = new BasicDBObject()
		dbobj.put("name", "test")
		db.createCollection(collectionName ,null)
		db.getCollection(collectionName).insert(dbobj)
	}
	
	@Test
	public void dropsACappedCollection() throws Exception {
		//Given
		givenACappedCollection(standalone, db)
		
		def builder = mongoUtils.dropCollection(dbName, cappedCollectionName)
		DBObject spec = builder.o
		
		//When
		operation.execute(db, spec)
		
		//Then
		def collectionExists = db.collectionExists(cappedCollectionName)
		assertThat collectionExists, is(false)
	}
	
	private givenACappedCollection(Mongo standalone, DB db) {
		DBObject options = new BasicDBObjectBuilder()
								.start()
									.add('capped', true)
									.add('size', 65536)
									.add('max', 2048)
								.get()
		db.createCollection(cappedCollectionName,options)
	}
	
	@Test
	public void shoutsWhenCollectionToBeDroppedDoesNotExistInTarget() throws Exception {
		//Given
		def builder = mongoUtils.dropCollection(dbName, absentCollectionName)
		DBObject spec = builder.o
		
		//When
		try {
			operation.execute(db, spec)
			fail("Should not drop collection that does not exist")
		} catch (DropCollectionFailed problem) {
		  //Then
		  assertThat problem.message, is("Could Not Drop Collection people")
		}
	}
}
