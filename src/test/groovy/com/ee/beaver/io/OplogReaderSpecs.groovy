package com.ee.beaver.io

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail
import static org.mockito.BDDMockito.given

import java.util.HashMap
import java.util.Map

import org.bson.types.BSONTimestamp
import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.ee.beaver.domain.MongoCollection
import com.ee.beaver.domain.MongoCollectionIterator
import com.ee.beaver.domain.operation.DeleteDocumentBuilder
import com.ee.beaver.domain.operation.DocumentBuilder
import com.ee.beaver.domain.operation.UpdateDocumentBuilder
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

@RunWith(MockitoJUnitRunner.class)
public class OplogReaderSpecs {

	@Mock
	private MongoCollection mockOplogCollection

	@Mock
	private MongoCollectionIterator<DBObject> mockOplogCollectionIterator

	private CollectionReader reader
	def objId
	private String db = 'beaver'
	private String collectionName = 'home'
	private String name = '[Test Name]'

	@Before
	public void setupOplogReader() {
		given(mockOplogCollection.find(false)).willReturn(
				mockOplogCollectionIterator)
		reader = new OplogReader(mockOplogCollection, false)
		objId = new ObjectId()
	}

	@Test
	public void readsACreateCollectionOperationDocument() {
		// Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(true)
		def document = new DocumentBuilder(
				ts: new BSONTimestamp(1352105652, 1),
				h: '3493050463814977392',
				op: 'c',
				ns: db + '.$cmd',
				o:  new BasicDBObjectBuilder().start().add( "create" , collectionName ).get()
				)
		given(mockOplogCollectionIterator.next()).willReturn(document as DBObject)

		// When
		String oplogDocumentString = reader.readDocument()

		// Then
		assertThat oplogDocumentString, is('{ "ts" : "{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}" , "h" : "3493050463814977392" , "op" : "c" , "ns" : "' + "$db" +'.$cmd" , "o" : "{ \\"create\\" : \\"'+collectionName+'\\"}"}')
	}

	@Test
	public void readsAnInsertOperationDocument() {
		// Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(true)
		def document = new DocumentBuilder(
				ts: new BSONTimestamp(1352105652, 1),
				h: '3493050463814977392',
				op: 'i',
				ns: "$db.$collectionName",
				o:  new BasicDBObjectBuilder().start()
				.add( "_id" , new BasicDBObject('$oid', objId))
				.add( "name" , name).get()
				)
		given(mockOplogCollectionIterator.next()).willReturn(document as DBObject)

		// When
		String oplogDocumentString = reader.readDocument()

		// Then
		assertThat oplogDocumentString, is('{ "ts" : "{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}" , "h" : "3493050463814977392" , "op" : "i" , "ns" : "' + "$db.$collectionName" + '" , "o" : "{ \\"_id\\" : { \\"$oid\\" : { \\"$oid\\" : \\"' + objId + '\\"}} , \\"name\\" : \\"' + name + '\\"}"}')
	}

	@Test
	public void readsAnUpdateOperationDocument() {
		// Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(true)
		def document = new UpdateDocumentBuilder(
				ts: new BSONTimestamp(1352105652, 1),
				h :'3493050463814977392',
				op :'u',
				ns : "$db.$collectionName",
				o2 : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.get(),
				o : new BasicDBObjectBuilder().start()
				.add('name', name)
				.get()
				)
		given(mockOplogCollectionIterator.next()).willReturn(document as DBObject)

		// When
		String oplogDocumentString = reader.readDocument()

		// Then
		assertThat oplogDocumentString, is ('{ "ts" : "{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}" , "h" : "3493050463814977392" , "op" : "u" , "ns" : "' + "$db.$collectionName" + '" , "o2" : "{ \\"_id\\" : { \\"$oid\\" : \\"' + objId + '\\"}}" , "o" : "{ \\"name\\" : \\"'+name+'\\"}"}')
	}

	@Test
	public void readsARemoveOperationDocument() {
		// Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(true)
		def document = new DeleteDocumentBuilder(
				ts: new BSONTimestamp(1352105652, 1),
				h :'3493050463814977392',
				op :'d',
				ns : "$db.$collectionName",
				b : true,
				o : new BasicDBObjectBuilder().start()
				.add('_id', objId)
				.get()
				)
		given(mockOplogCollectionIterator.next()).willReturn(document as DBObject)

		// When
		String oplogDocumentString = reader.readDocument()

		// Then
		assertThat oplogDocumentString, is('{ "ts" : "{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}" , "h" : "3493050463814977392" , "op" : "d" , "ns" : "' + "$db.$collectionName" + '" , "b" : true , "o" : "{ \\"_id\\" : { \\"$oid\\" : \\"' + objId + '\\"}}"}')
	}

	@Test
	public void readsADropCollectionOperationDocument() {
		// Given
		given(mockOplogCollectionIterator.hasNext()).willReturn(true)
		String collectionName = 'home'
		def document = new DocumentBuilder(
				ts: new BSONTimestamp(1352105652, 1),
				h :'3493050463814977392',
				op :'c',
				ns : "$db" + '.$cmd',
				o : new BasicDBObjectBuilder().start()
				.add('drop', collectionName)
				.get()
				)
		given(mockOplogCollectionIterator.next()).willReturn(document as DBObject)

		// When
		String oplogDocumentString = reader.readDocument()

		// Then
		assertThat	oplogDocumentString, is ('{ "ts" : "{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}" , "h" : "3493050463814977392" , "op" : "c" , "ns" : "' + "$db" +'.$cmd" , "o" : "{ \\"drop\\" : \\"'+ collectionName +'\\"}"}')

	}

	@Test
	public void shoutsWhenQueryingForDocumentWithAClosedReader() {
		// When
		reader.close()

		try {
			reader.hasDocument()
			fail("Should Not Allow To Work With A closed Reader")
		} catch (ReaderAlreadyClosed rac) {
			assertThat rac.getMessage(), is("Reader Already Closed")
		}
	}

	@Test
	public void shoutsWhenFetchingForDocumentWithAClosedReader() {
		// When
		reader.close()

		try {
			reader.readDocument()
			fail("Should Not Allow To Work With A closed Reader")
		} catch (ReaderAlreadyClosed rac) {
			assertThat rac.getMessage(), is("Reader Already Closed")
		}
	}
}
