package com.ee.beaver.io

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.fail
import static org.mockito.Mockito.doThrow
import static org.mockito.Mockito.verify

import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.ee.beaver.domain.operation.MongoUtils
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder

@RunWith(MockitoJUnitRunner.class)
public class TimestampWriterSpecs {

	@Mock
	private Writer mockTargetWriter
	
	private TimestampWriter timestampWriter
	private String dbName = 'beaver'
	private String collectionName = 'home'
	private String name = '[Test Name]'
	def objId = new ObjectId()
	def anotherObjId = new ObjectId()
	
	@Before
	public void givenThereExists() {
		timestampWriter = new TimestampWriter(mockTargetWriter)
	}

	def getDocumentString(ObjectId objId) {
		def o = new BasicDBObjectBuilder()
					.start()
						.add( "_id" , new BasicDBObject('$oid', objId))
						.add( "name" , name)
					.get()
		
		MongoUtils.insertDocument(dbName,collectionName, o) as String
	}

	@Test
	public void writesTimestampToDestination() throws IOException {
		// When
		String document = getDocumentString(objId)
		timestampWriter.write(document, 0, document.length())

		// Then
		String expectedTimestamp = ('"ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}')
		assertThat timestampWriter.getTimestamp(), is(expectedTimestamp)
	}


	@Test
	public void delegatesWritesToTargetWriter() throws IOException {
		// When
		String document = getDocumentString(objId)
		timestampWriter.write(document, 0, document.length())

		// Then
		verify(mockTargetWriter).append(document, 0, document.length())
	}

	@Test
	public void writesTimestampOfLastDocumentReadToDestination() throws IOException {
		// Given
		String documentOne = getDocumentString(objId)
		timestampWriter.write(documentOne, 0, documentOne.length())

		//When
		String documentTwo = getDocumentString(anotherObjId)
		timestampWriter.write(documentTwo, 0, documentTwo.length())

		// Then
		String expectedTimestamp = ('"ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1}')
		assertThat(timestampWriter.getTimestamp(), is(expectedTimestamp))
	}

	@Test
	public void doesNotWriteTimestampWhenDelegateWriterFails()
	throws IOException {
		// Given
		String documentOne = getDocumentString(objId)
		timestampWriter.write(documentOne, 0, documentOne.length())
		
		String lastRecordedTimestamp = timestampWriter.getTimestamp()

		String documentTwo = getDocumentString(anotherObjId)
		doThrow(new IOException("Disk Full")).when(mockTargetWriter).append(documentTwo,
				0, documentTwo.length())

		// When
		try {
			timestampWriter.write(documentTwo, 0, documentTwo.length())
			fail("Should not have written timestamp!")
		} catch (IOException e) {
			// Then
			assertThat(timestampWriter.getTimestamp(), is(lastRecordedTimestamp))
		}
	}

	@Test
	public void writesTimestampOnlyIfDocumentHasTimestampEntry() throws Exception {
		//Given
		String document = new BasicDBObjectBuilder()
							.start()
								.add("name", "test")
							.get()
							.toString()

		//When
		timestampWriter.write(document , 0, document.length())

		// Then
		assertThat(timestampWriter.getTimestamp(), is(""))
	}
	
}
