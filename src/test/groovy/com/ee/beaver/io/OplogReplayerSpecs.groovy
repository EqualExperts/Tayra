package com.ee.beaver.io

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.mockito.BDDMockito.*
import static org.mockito.Mockito.*

import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.ee.beaver.domain.operation.*
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

@RunWith(MockitoJUnitRunner.class)
public class OplogReplayerSpecs {
	
	private OplogReplayer replayer
	private String collectionName = 'home'
	private MongoUtils mongoUtils = new MongoUtils()
	def objId = new ObjectId('509754dd2862862d511f6b57')
	def dbName = 'beaver'
	def name = '[Test Name]'
	
	@Mock
	private OperationsFactory mockOperations
	
	@Mock
	private Operation mockOperation
	
	@Before
	public void given() {
		replayer = new OplogReplayer(mockOperations)
	}

	@Test
	public void replaysCreateCollectionOperation() throws Exception {
		//Given
		def builder = mongoUtils.createCollection(dbName, collectionName)
		DBObject spec = builder.o
		
		given(mockOperations.get('c')).willReturn(mockOperation)
		
		//When
		replayer.replayDocument(builder as String)
		
		//Then
		verify(mockOperation).execute(builder as DBObject)
	}
	
	@Test
	public void replaysInsertDocumentOperation() throws Exception {
		//Given
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
						.add('name', name)
					.get()
		def builder = mongoUtils.insertDocument(dbName, collectionName, o)
		
		given(mockOperations.get('i')).willReturn(mockOperation)
		
		//When
		replayer.replayDocument(builder as String)
		
		//Then
		verify(mockOperation).execute(builder as DBObject)
	}
}
