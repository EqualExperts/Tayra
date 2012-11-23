package com.ee.beaver.io

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.hamcrest.Matchers.is
import static org.junit.Assert.fail
import static org.mockito.Mockito.doThrow
import static org.mockito.Mockito.verify

import java.io.IOException
import java.io.Writer
import java.util.HashMap
import java.util.Map

import org.bson.types.BSONTimestamp
import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.ee.beaver.domain.operation.DocumentBuilder
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import com.sun.xml.internal.txw2.Document

@RunWith(MockitoJUnitRunner.class)
public class TimestampWriterSpecs {

	@Mock
	private Writer mockTargetWriter
	private TimestampWriter timestampWriter
	def objId

	@Before
	public void givenThereExists() {
		timestampWriter = new TimestampWriter(mockTargetWriter)
		objId = new ObjectId()
	}

	private DocumentBuilder oplogDocumentOne(ObjectId objId) {
		def oplogDocument = new DocumentBuilder(
				ts: new BSONTimestamp(1352094941, 1),
				h: '3493050463814977392',
				op: 'i',
				ns: 'person.things',
				o:  new BasicDBObjectBuilder().start()
				.add( "_id" , new BasicDBObject('$oid', objId))
				.add( "name" , "[Test Name]").get()
				)
		return oplogDocument
	}

	private DocumentBuilder oplogDocumentTwo(ObjectId objId) {
		def oplogDocument = new DocumentBuilder(
				ts: new BSONTimestamp(1352094942, 1),
				h: '3493050463814977392',
				op: 'i',
				ns: 'person.things',
				o:  new BasicDBObjectBuilder().start()
				.add( "_id" , new BasicDBObject('$oid', objId))
				.add( "name" , "[Test Name]").get()
				)
		return oplogDocument
	}

	@Test
	public void writesTimestamptoDestination() throws IOException {
		// When
		def oplogDocument = oplogDocumentOne(objId)
		String document = oplogDocument as String
		timestampWriter.write(document, 0, document.length())

		// Then
		String expectedTimestamp = ('"ts":"{ \\"$ts\\" : 1352094941 , \\"$inc\\" : 1}')
		assertThat timestampWriter.getTimestamp(), is(expectedTimestamp)
	}


	@Test
	public void delegatesWritesToTargetWriter() throws IOException {
		// When
		def oplogDocument = oplogDocumentOne(objId)
		String document = oplogDocument as String
		timestampWriter.write(document, 0, document.length())

		// Then
		verify(mockTargetWriter).append(document, 0, document.length())
	}

	@Test
	public void writesTimestampOfLastDocumentReadToDestination()
	throws IOException {
		// Given
		def oplogDocumentOne = oplogDocumentOne(objId)
		String documentOne = oplogDocumentOne as String
		timestampWriter.write(documentOne, 0, documentOne.length())

		//When
		def oplogDocumentTwo = oplogDocumentTwo(objId)
		String documentTwo = oplogDocumentTwo as String
		timestampWriter.write(documentTwo, 0, documentTwo.length())

		// Then
		String expectedTimestamp = ('"ts":"{ \\"$ts\\" : 1352094942 , \\"$inc\\" : 1}')
		assertThat(timestampWriter.getTimestamp(), is(expectedTimestamp))
	}

	@Test
	public void doesNotWriteTimestampWhenDelegateWriterFails()
	throws IOException {
		// Given
		def oplogDocumentOne = oplogDocumentOne(objId)
		String documentOne = oplogDocumentOne as String
		timestampWriter.write(documentOne, 0, documentOne.length())
		String lastRecordedTimestamp = timestampWriter.getTimestamp()

		def oplogDocumentTwo = oplogDocumentTwo(objId)
		String documentTwo = oplogDocumentTwo as String
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
				.start().add("name", "test")
				.get()
				.toString()

		//When
		timestampWriter.write(document , 0, document.length())

		// Then
		assertThat(timestampWriter.getTimestamp(), is(""))
	}


}
