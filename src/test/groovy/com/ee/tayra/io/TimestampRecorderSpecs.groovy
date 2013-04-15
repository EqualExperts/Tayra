package com.ee.tayra.io

import org.bson.types.ObjectId

import spock.lang.*

import com.ee.tayra.domain.operation.MongoUtils
import com.ee.tayra.io.TimestampRecorder;
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder

public class TimestampRecorderSpecs extends Specification {

	private DocumentWriter mockTargetWriter

	private TimestampRecorder timestampRecorder
	private String dbName = 'tayra'
	private String collectionName = 'home'
	private String name = '[Test Name]'
	def objId = new ObjectId()
	def anotherObjId = new ObjectId()

	def setup() {
		mockTargetWriter = Mock(DocumentWriter)
		timestampRecorder = new TimestampRecorder(mockTargetWriter)
	}

	def getDocumentString(ObjectId objId) {
		def o = new BasicDBObjectBuilder()
					.start()
						.add( "_id" , new BasicDBObject('$oid', objId))
						.add( "name" , name)
					.get()

		MongoUtils.insertDocument(dbName,collectionName, o) as String
	}

	def writesTimestampToDestination() throws IOException {
		given: 'an insert document oplog entry'
			String document = getDocumentString(objId)

		when: 'it writes the document'
			timestampRecorder.writeDocument(document)

		then: 'destination should have the expected timestamp'
			timestampRecorder.getTimestamp() == ('{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }')
	}

	def delegatesWritesToTargetWriter() throws IOException {
		given: 'an insert document oplog entry'
			String document = getDocumentString(objId)

		when: 'it writes the document'
			timestampRecorder.writeDocument(document)

		then: 'the delegate writer should write the document'
			1 * mockTargetWriter.writeDocument(document)
	}

	def writesTimestampOfLastDocumentReadToDestination() throws IOException {
		given: 'two insert document oplog entries'
			String documentOne = getDocumentString(objId)
			String documentTwo = getDocumentString(anotherObjId)

		and: 'document one is already written'
			timestampRecorder.writeDocument(documentOne)

		when: 'it writes document two'
			timestampRecorder.writeDocument(documentTwo)

		then: 'destination should have timestamp of document two'
			timestampRecorder.getTimestamp() == ('{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }')
	}

	def doesNotWriteTimestampWhenDelegateWriterFails() throws IOException {
		given: 'two insert document oplog entries'
			String documentOne = getDocumentString(objId)
			String documentTwo = getDocumentString(anotherObjId)

		and: 'document one is already written'
			timestampRecorder.writeDocument(documentOne)

		and: 'destination holds its timestamp'
			String lastRecordedTimestamp = timestampRecorder.getTimestamp()

		and: 'delegate writer fails to write document two'
			mockTargetWriter.writeDocument(documentTwo) >> {throw new IOException("Disk Full")}

		when: 'it tries to write document two'
			timestampRecorder.writeDocument(documentTwo)

		then: 'destination should have timestamp of latest successful write'
			timestampRecorder.getTimestamp() == lastRecordedTimestamp
			thrown(IOException)
	}

	def writesTimestampOnlyIfDocumentHasTimestampEntry() throws Exception {
		given: 'a document without timestamp'
			String document = new BasicDBObjectBuilder()
								.start()
									.add("name", "test")
								.get()
								.toString()

		when: 'it writes the document'
			timestampRecorder.writeDocument(document)

		then: 'no timestamp should be written to destination'
			timestampRecorder.getTimestamp() == ""
	}

}
