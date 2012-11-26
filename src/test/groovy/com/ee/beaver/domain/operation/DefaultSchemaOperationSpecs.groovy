package com.ee.beaver.domain.operation

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.mockito.BDDMockito.*
import static org.mockito.Mockito.*

import org.bson.types.BSONTimestamp
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import com.mongodb.Mongo


@RunWith(MockitoJUnitRunner.class)
class DefaultSchemaOperationSpecs extends RequiresMongoConnection {

	DefaultSchemaOperation defaultSchemaOperation
	private String collectionName = 'home'

	@Mock
	private SchemaOperation mockSchemaOperation

	@Mock
	private SchemaOperationsFactory mockSchemaOperationsFactory

	@Before
	public void setup() {
		defaultSchemaOperation = new DefaultSchemaOperation(standalone,mockSchemaOperationsFactory)
	}

	@Test
	public void performsCorrectSchemaOperation() {
		//Given
		def document = new DocumentBuilder(
						ts: new BSONTimestamp(1352105652, 1),
						h :'3493050463814977392',
						op :'c',
						ns : db + '.$cmd',
						o : new BasicDBObjectBuilder()
							.start()
								.add('create', collectionName)
								.add('capped', false)
								.add('size', null)
								.add('max', null)
							.get()
					)
		DBObject spec = new BasicDBObjectBuilder()
							.start()
								.add('create', collectionName)
								.add('capped', false)
								.add('size', null)
								.add('max', null)
							.get()
		given(mockSchemaOperationsFactory.from(spec)).willReturn(mockSchemaOperation)
		
		//When
		defaultSchemaOperation.execute(document as DBObject)
		
		//Then
		verify(mockSchemaOperation).execute(standalone.getDB(db), spec)
	}
	
}

