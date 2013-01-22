package com.ee.tayra.domain.operation

import com.ee.tayra.domain.operation.CreateCollection;
import com.ee.tayra.domain.operation.DropCollection;
import com.ee.tayra.domain.operation.DropDatabase;
import com.ee.tayra.domain.operation.SchemaOperation;
import com.ee.tayra.domain.operation.SchemaOperationsFactory;
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import com.mongodb.Mongo

class SchemaOperationsFactorySpecs extends RequiresMongoConnection {

	private String collectionName = 'home'
	def schemaOperationsFactory

	def setup() {
		schemaOperationsFactory = new SchemaOperationsFactory(standalone)
	}
	
	def producesCreateCollectionOperation() throws Exception {
		given: 'a create collection oplog entry payload'
			def builder = MongoUtils.createCollection(dbName, collectionName)
			DBObject spec = builder.o

		when: 'SchemaOperation instance is created'
			SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)
		
		then: 'it creates CreateCollection instance'
			schemaOperation instanceof CreateCollection
	}
	
	def producesDropCollectionOperation() throws Exception {
		given: 'a drop collection oplog entry payload'
			def builder = MongoUtils.dropCollection(dbName, collectionName)
			DBObject spec = builder.o
		
		when: 'SchemaOperation instance is created'
			SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)
		
		then: 'it creates DropCollection instance'
			schemaOperation instanceof DropCollection
	}
	
	def producesDropDatabaseOperation() throws Exception {
		given: 'a drop database oplog entry payload'
			def builder = MongoUtils.dropDatabase("SomeDbName")
			DBObject spec = builder.o
		
		when: 'SchemaOperation instance is created'
			SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)
		
		then: 'it creates DropDatabase instance'
			schemaOperation instanceof DropDatabase
	}
	
	def producesaNoOperationForInvalidField() throws Exception {
		given: 'an invalid oplog entry payload'
			DBObject spec = new BasicDBObjectBuilder()
								.start()
									.add('invalid', 1)
								.get()
								
		when: 'SchemaOperation instance is created'
			SchemaOperation schemaOperation = schemaOperationsFactory.from(spec)

		then: 'No operation is performed'
			schemaOperation == SchemaOperation.NO_OP
	}
	
}
