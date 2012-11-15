package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

import java.net.UnknownHostException

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.CommandResult
import com.mongodb.DB
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoException

import org.bson.types.BSONTimestamp
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.After
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.fail

class CreateCollectionSpecs extends RequiresMongoConnection {
	def operation
	private String collectionName = 'home'
	
	@Before
	public void setUp() {
		operation = new CreateCollection(standalone)
	}
	
	@After
	public void cleanUp() {
		standalone.getDB(db).getCollection(collectionName).drop()
	}

	@Test
	public void createsACollection() throws Exception {
		//Given
		def document = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : db + '.$cmd',
			o : new BasicDBObjectBuilder().start()
					.add('create', collectionName)
					.add('capped', false)
					.add('size', null)
					.add('max', null)
					.get()
		)
		//When
		operation.execute(document as DBObject)
		
		//Then
		def collectionExists = standalone.getDB(db).collectionExists(collectionName)
		assertThat collectionExists, is(true)    
	}
	
	@Test
	public void shoutsWhenACollectionAlreadyExists() {
		def document = new DocumentBuilder(
				ts: new BSONTimestamp(1352105652, 1),
				h :'3493050463814977392',
				op :'c',
				ns : db + '.$cmd',
				o : new BasicDBObjectBuilder().start()
				.add('create', collectionName)
				.add('capped', false)
				.add('size', null)
				.add('max', null)
				.get()
				)
		
		//When
		operation.execute(document as DBObject)
		try {
			operation.execute(document as DBObject)
		    fail("Should not have created collection: $collectionName, as it already exists!")
		} catch (CreateCollectionFailed problem) {
		  assertThat problem.message, is("command failed [command failed [create] { \"serverUsed\" : \"localhost:27020\" , \"errmsg\" : \"collection already exists\" , \"ok\" : 0.0}")
		}
	}
	
	@Test
	public void createsACappedCollection() throws Exception {
		//Given
		def document = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h :'3493050463814977392',
			op :'c',
			ns : db + '.$cmd',
			o : new BasicDBObjectBuilder().start()
					.add('create', collectionName)
					.add('capped', true)
					.add('size', 65536)
					.add('max', 2048)
					.get()
		)
		//When
		operation.execute(document as DBObject)
		
		//Then
		DB db = standalone.getDB(db)
		assertThat db.collectionExists(collectionName), is(true)
		
		CommandResult result = db.getCollection(collectionName).getStats()
		assertThat result.get('capped'), is(true)    
		assertThat result.get('max'), is(2048)    
	}
}
