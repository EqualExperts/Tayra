package com.ee.beaver.io

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.mockito.Mockito.*
import static org.mockito.BDDMockito.*

import org.bson.BSONObject
import org.bson.types.BSONTimestamp
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.ee.beaver.domain.operation.*;
import com.ee.beaver.io.OplogReplayer
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DB
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoException
import org.bson.types.ObjectId

@RunWith(MockitoJUnitRunner.class)
public class OplogReplayerSpecs {
	
	private OplogReplayer replayer
	
	@Mock
	private OperationsFactory mockOperations
	
	@Mock
	private Operation mockOperation
	
	@Before
	public void setup() {
		replayer = new OplogReplayer(mockOperations)
	}

	@Test
	public void replaysCreateCollectionOperation() throws Exception {
		//Given
		def document = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1), 
			h: '3493050463814977392',
			op: 'c',
			ns: 'person.$cmd',
			o:  new BasicDBObjectBuilder().start().add( "create" , "testCollection" ).get()
		)
		given(mockOperations.get('c')).willReturn(mockOperation)
		
		//When
		replayer.replayDocument(document as String)
		
		//Then
		verify(mockOperation).execute(document as DBObject)
	}
	
	@Test
	public void replaysInsertDocumentOperation() throws Exception {
		//Given
		def document = new DocumentBuilder(
			ts: new BSONTimestamp(1352105652, 1),
			h: '3493050463814977392',
			op: 'i',
			ns: 'person.things',
			o:  new BasicDBObjectBuilder().start()
				.add( "_id" , new BasicDBObject('$oid', new ObjectId()))
				.add( "name" , "[Test Name]").get()
		)
		given(mockOperations.get('i')).willReturn(mockOperation)
		
		//When
		replayer.replayDocument(document as String)
		
		//Then
		verify(mockOperation).execute(document as DBObject)
	}
}
