package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bson.types.BSONTimestamp
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import com.mongodb.BasicDBObject
import static org.junit.Assert.fail
import com.mongodb.Mongo

class DropCollectionSpecs extends RequiresMongoConnection {
	def operation
	private String collectionName = 'home'
	private String cappedCollectionName = 'person'
	private String absentCollectionName = 'people'
	
	@Before
	public void setUp() {
		operation = new DatabaseCommand(standalone)
	}
	
	@Test
	public void dropsACollection() throws Exception {
		//Given
		givenACollection()
		
		def document = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : db + '.$cmd',
			o : new BasicDBObjectBuilder().start()
					.add('drop', collectionName)
					.get()
		)
		//When
		operation.execute(document as DBObject)
		
		//Then
		def collectionExists = standalone.getDB(db).collectionExists(collectionName)
		assertThat collectionExists, is(false)
	}

	private givenACollection() {
		BasicDBObject dbobj = new BasicDBObject()
		dbobj.put("name", "abc")
		standalone.getDB(db).createCollection(collectionName ,null)
		standalone.getDB(db).getCollection(collectionName).insert(dbobj)
	}
	
	@Test
	public void dropsACappedCollection() throws Exception {
		//Given
		givenACappedCollection(standalone, db)

		def document = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : db + '.$cmd',
			o : new BasicDBObjectBuilder().start()
					.add('drop', cappedCollectionName)
					.get()
		)
		//When
		operation.execute(document as DBObject)
		
		//Then
		def collectionExists = standalone.getDB(db).collectionExists(cappedCollectionName)
		assertThat collectionExists, is(false)
	}

	private givenACappedCollection(Mongo standalone, String db) {
		DBObject options = new BasicDBObjectBuilder().start()
				.add('capped', true)
				.add('size', 65536)
				.add('max', 2048)
				.get()
		standalone.getDB(db).createCollection(cappedCollectionName,options)
	}
	
	@Test
	public void shoutsWhenCollectionToBeDroppedDoesNotExistInTarget() throws Exception {
		//Given
		def document = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : db + '.$cmd',
			o : new BasicDBObjectBuilder().start()
					.add('drop', absentCollectionName)
					.get()
		)
		
		//When
		try {
			operation.execute(document as DBObject)
			fail("Should not drop collection that does not exist")
		} catch (DropCollectionFailed problem) {
		  //Then
		  assertThat problem.message, is("Could Not Drop Collection people")
		}
	}
}
