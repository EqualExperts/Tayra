package com.ee.beaver.domain.operation

import java.net.UnknownHostException;
import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import com.mongodb.Mongo;
import com.mongodb.MongoException;

class SchemaOperationsFactorySpecs {
	private static Mongo standalone;
	private static final String HOST = "localhost";
	private static final int PORT = 27020;
	private String collectionName = 'home'
	def schemaOperationsFactory

	@BeforeClass
	public static void connectToMongo() throws UnknownHostException,
			MongoException {
		standalone = new Mongo(HOST, PORT);
	}

	@AfterClass
	public static void closeConnectionToMongo() {
		standalone.close();
	}
	
	@Before
	public void setup() {
		schemaOperationsFactory = new SchemaOperationsFactory(standalone)
	}
	
	@Test
	public void producesCreateCollectionOperation() throws Exception {
		//Given
		DBObject spec = new BasicDBObjectBuilder().start()
							.add('create', collectionName)
							.add('capped', false)
							.add('size', null)
							.add('max', null)
							.get()
		
		//When
		SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)
		
		//Then
		assertThat schemaOperation, instanceOf(CreateCollection.class)
	}
	
	@Test
	public void producesDropCollectionOperation() throws Exception {
		//Given
		DBObject spec = new BasicDBObjectBuilder().start()
							.add('drop', collectionName)
							.get()
		//When
		SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)
		
		//Then
		assertThat schemaOperation, instanceOf(DropCollection.class)
	}
	
	@Test
	public void producesDropDatabaseOperation() throws Exception {
		//Given
		DBObject spec = new BasicDBObjectBuilder().start()
							.add('dropDatabase', 1)
							.get()
		//When
		SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)
		
		//Then
		assertThat schemaOperation, instanceOf(DropDatabase.class)
	}
	
	@Test
	public void producesaNoOperationForInvalidField() throws Exception {
		//Given
		DBObject spec = new BasicDBObjectBuilder().start()
							.add('invalid', 1)
							.get()
		//When
		SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)

		//Then
		assertThat schemaOperation, sameInstance(SchemaOperation.NO_OP)
	}
	
}
