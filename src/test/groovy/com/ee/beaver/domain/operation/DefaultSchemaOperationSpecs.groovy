package com.ee.beaver.domain.operation

import com.mongodb.DBObject
import com.mongodb.Mongo

class DefaultSchemaOperationSpecs extends RequiresMongoConnection {

	DefaultSchemaOperation defaultSchemaOperation
	private String collectionName = 'home'
	
	private SchemaOperation mockSchemaOperation

	private SchemaOperationsFactory mockSchemaOperationsFactory

	def setup() {
		mockSchemaOperationsFactory = Stub(SchemaOperationsFactory)
		mockSchemaOperation = Mock(SchemaOperation)
		defaultSchemaOperation = new DefaultSchemaOperation(standalone,mockSchemaOperationsFactory)
	}

	def performsCorrectSchemaOperation() {
		given: 'a create collection oplog entry and its payload'
			def builder = MongoUtils.createCollection(dbName, collectionName)
			DBObject spec = builder.o
		
		and: 'SchemaOperations factory gets a Create Collection Operation'
			mockSchemaOperationsFactory.from(spec) >> mockSchemaOperation
		
		when: 'the operation runs'
			defaultSchemaOperation.doExecute(builder as DBObject)
		
		then: 'Create Collection Operation executes the oplog entry'
			1 * mockSchemaOperation.doExecute(standalone.getDB(dbName), spec)
	}
	
}

