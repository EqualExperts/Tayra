package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

import groovyjarjarcommonscli.Options;

import java.sql.DatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bson.types.BSONTimestamp
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DB
import com.mongodb.DBObject
import com.mongodb.BasicDBObject
import static org.junit.Assert.fail
import com.mongodb.Mongo
import com.sun.java.util.jar.pack.CodingChooser.Sizer;

class DropCollectionSpecs extends RequiresMongoConnection {
	def operation
	private String collectionName = 'home'
	private String cappedCollectionName = 'person'
	private String absentCollectionName = 'people'
	private DB database
	
	@Before
	public void setUp() {
		operation = new DropCollection()
		database = standalone.getDB(db)
	}
	
	@Test
	public void dropsACollection() throws Exception {
		//Given
		givenACollection()

		DBObject spec = new BasicDBObjectBuilder().start()
							.add('drop', collectionName)
							.get()

		//When
		operation.execute(database, spec)
		
		//Then
		def collectionExists = database.collectionExists(collectionName)
		assertThat collectionExists, is(false)
	}

	private givenACollection() {
		BasicDBObject dbobj = new BasicDBObject()
		dbobj.put("name", "abc")
		database.createCollection(collectionName ,null)
		database.getCollection(collectionName).insert(dbobj)
	}
	
	@Test
	public void dropsACappedCollection() throws Exception {
		//Given
		givenACappedCollection(standalone, db)

		DBObject spec = new BasicDBObjectBuilder().start()
							.add('drop', cappedCollectionName)
							.get()
		
		//When
		operation.execute(database, spec)
		
		//Then
		def collectionExists = database.collectionExists(cappedCollectionName)
		assertThat collectionExists, is(false)
	}
	
	def createCollection(name, isCapped = false, size = null, max = null) {
		DBObject options = new BasicDBObjectBuilder()
							.start()
								.add('capped', isCapped)
								.add('size', size)
								.add('max', max)
								.get()
	}

	private givenACappedCollection(Mongo standalone, String db) {
		DBObject options = new BasicDBObjectBuilder().start()
				.add('capped', true)
				.add('size', 65536)
				.add('max', 2048)
				.get()
		database.createCollection(cappedCollectionName,options)
	}
	
	@Test
	public void shoutsWhenCollectionToBeDroppedDoesNotExistInTarget() throws Exception {
		//Given
		DBObject spec = new BasicDBObjectBuilder().start()
							.add('drop', absentCollectionName)
							.get()
		//When
		try {
			operation.execute(database, spec)
			fail("Should not drop collection that does not exist")
		} catch (DropCollectionFailed problem) {
		  //Then
		  assertThat problem.message, is("Could Not Drop Collection people")
		}
	}
}
