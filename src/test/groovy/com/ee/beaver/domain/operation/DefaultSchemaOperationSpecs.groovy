package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.mockito.BDDMockito.*
import static org.mockito.Mockito.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.mongodb.DBObject
import com.mongodb.Mongo

@RunWith(MockitoJUnitRunner.class)
class DefaultSchemaOperationSpecs extends RequiresMongoConnection {

	DefaultSchemaOperation defaultSchemaOperation
	private String collectionName = 'home'
	private MongoUtils mongoUtils = new MongoUtils()
	
	@Mock
	private SchemaOperation mockSchemaOperation

	@Mock
	private SchemaOperationsFactory mockSchemaOperationsFactory

	@Before
	public void givenADefaultSchemaOperation() {
		defaultSchemaOperation = new DefaultSchemaOperation(standalone,mockSchemaOperationsFactory)
	}

	@Test
	public void performsCorrectSchemaOperation() {
		//Given
		def builder = mongoUtils.createCollection(dbName, collectionName)
		DBObject spec = builder.o

		given(mockSchemaOperationsFactory.from(spec)).willReturn(mockSchemaOperation)
		
		//When
		defaultSchemaOperation.execute(builder as DBObject)
		
		//Then
		verify(mockSchemaOperation).execute(standalone.getDB(dbName), spec)
	}
	
}

