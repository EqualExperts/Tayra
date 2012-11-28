package com.ee.beaver.io

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail
import static org.mockito.BDDMockito.given

import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.ee.beaver.domain.MongoCollection
import com.ee.beaver.domain.MongoCollectionIterator
import com.ee.beaver.domain.operation.MongoUtils
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

@RunWith(MockitoJUnitRunner.class)
public class OplogReaderSpecs {

	@Mock
	private MongoCollection mockOplogCollection

	@Mock
	private MongoCollectionIterator<DBObject> mockOplogCollectionIterator
	
	private CollectionReader reader
	private String dbName = 'beaver'
	private String collectionName = 'home'
	private String name = '[Test Name]'
	def objId

	@Before
	public void givenAnOplogReader() {
		given(mockOplogCollection.find(false)).willReturn(mockOplogCollectionIterator)
		reader = new OplogReader(mockOplogCollection, false)
		objId = new ObjectId()
	}

	@Test
	public void readsACreateCollectionOperationDocument() {
		// Given
		def document = MongoUtils.createCollection(dbName, collectionName) as DBObject
		
		given(mockOplogCollectionIterator.hasNext()).willReturn(true)
		given(mockOplogCollectionIterator.next()).willReturn(document)

		// When
		String oplogDocumentString = reader.readDocument()
		
		// Then
		assertThat oplogDocumentString, is('{ "ts" : "{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}" , \"h\" : \"3493050463814977392\" , \"op\" : \"c\" , \"ns\" : \"' + dbName + '.$cmd\" , \"o\" : \"{ \\"create\\" : \\"' + collectionName + '\\" , \\"capped\\" : false , \\"size\\" :  null  , \\"max\\" :  null }"}')
	}

	@Test
	public void readsAnInsertOperationDocument() {
		// Given
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
						.add('name', name)
					.get()
		def document = MongoUtils.insertDocument(dbName, collectionName, o) as DBObject
		
		given(mockOplogCollectionIterator.hasNext()).willReturn(true)
		given(mockOplogCollectionIterator.next()).willReturn(document)

		// When
		String oplogDocumentString = reader.readDocument()

		// Then
		assertThat oplogDocumentString, is('{ "ts" : "{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}" , "h" : "3493050463814977392" , "op" : "i" , "ns" : "' + "$dbName.$collectionName" + '" , "o" : "{ \\"_id\\" : { \\"$oid\\" : \\"' + objId + '\\"} , \\"name\\" : \\"' + name + '\\"}"}')
	}

	@Test
	public void readsAnUpdateOperationDocument() {
		// Given
		def o2 = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
					.get()
				
		def o = new BasicDBObjectBuilder()
					.start()
						.add('name', name)
					.get()

		def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as DBObject
		
		given(mockOplogCollectionIterator.hasNext()).willReturn(true)
		given(mockOplogCollectionIterator.next()).willReturn(document)

		// When
		String oplogDocumentString = reader.readDocument()

		// Then
		assertThat oplogDocumentString, is ('{ "ts" : "{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}" , "h" : "3493050463814977392" , "op" : "u" , "ns" : "' + "$dbName.$collectionName" + '" , "o2" : "{ \\"_id\\" : { \\"$oid\\" : \\"' + objId + '\\"}}" , "o" : "{ \\"name\\" : \\"'+name+'\\"}"}')
	}

	@Test
	public void readsARemoveOperationDocument() {
		// Given
		def o = new BasicDBObjectBuilder()
					.start()
						.add('_id', objId)
					.get()
		def document = MongoUtils.deleteDocument(dbName, collectionName,o) as DBObject
		
		given(mockOplogCollectionIterator.hasNext()).willReturn(true)
		given(mockOplogCollectionIterator.next()).willReturn(document)

		// When
		String oplogDocumentString = reader.readDocument()

		// Then
		assertThat oplogDocumentString, is('{ "ts" : "{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}" , "h" : "3493050463814977392" , "op" : "d" , "ns" : "' + "$dbName.$collectionName" + '" , "b" : true , "o" : "{ \\"_id\\" : { \\"$oid\\" : \\"' + objId + '\\"}}"}')
	}

	@Test
	public void readsADropCollectionOperationDocument() {
		// Given
		def document = MongoUtils.dropCollection(dbName, collectionName) as DBObject

		given(mockOplogCollectionIterator.hasNext()).willReturn(true)
		given(mockOplogCollectionIterator.next()).willReturn(document)

		// When
		String oplogDocumentString = reader.readDocument()

		// Then
		assertThat	oplogDocumentString, is ('{ "ts" : "{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}" , "h" : "3493050463814977392" , "op" : "c" , "ns" : "' + "$dbName" +'.$cmd" , "o" : "{ \\"drop\\" : \\"'+ collectionName +'\\"}"}')

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
