package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoException

class SchemaOperationsFactorySpecs extends RequiresMongoConnection {

	private String collectionName = 'home'
	private MongoUtils mongoUtils = new MongoUtils()
	def schemaOperationsFactory

	@Before
	public void given() {
		schemaOperationsFactory = new SchemaOperationsFactory(standalone)
	}
	
	@Test
	public void producesCreateCollectionOperation() throws Exception {
		//Given
		def builder = mongoUtils.createCollection(dbName, collectionName)
		DBObject spec = builder.o

		//When
		SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)
		
		//Then
		assertThat schemaOperation, instanceOf(CreateCollection.class)
	}
	
	@Test
	public void producesDropCollectionOperation() throws Exception {
		//Given
		def builder = mongoUtils.dropCollection(dbName, collectionName)
		DBObject spec = builder.o
		
		//When
		SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)
		
		//Then
		assertThat schemaOperation, instanceOf(DropCollection.class)
	}
	
	@Test
	public void producesDropDatabaseOperation() throws Exception {
		//Given
		def builder = mongoUtils.dropDatabase("SomeDbName")
		DBObject spec = builder.o
		
		//When
		SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)
		
		//Then
		assertThat schemaOperation, instanceOf(DropDatabase.class)
	}
	
	@Test
	public void producesaNoOperationForInvalidField() throws Exception {
		//Given
		DBObject spec = new BasicDBObjectBuilder()
							.start()
								.add('invalid', 1)
							.get()
		//When
		SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)

		//Then
		assertThat schemaOperation, sameInstance(SchemaOperation.NO_OP)
	}
	
}
