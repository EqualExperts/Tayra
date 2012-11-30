package com.ee.beaver.io

import org.bson.types.ObjectId

import spock.lang.*

import com.ee.beaver.domain.operation.MongoUtils
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder

public class TimestampWriterSpecs extends Specification {

	private Writer mockTargetWriter

	private TimestampWriter timestampWriter
	private String dbName = 'beaver'
	private String collectionName = 'home'
	private String name = '[Test Name]'
	def objId = new ObjectId()
	def anotherObjId = new ObjectId()

	def setup() {
		mockTargetWriter = Mock(Writer)
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

	def writesTimestampToDestination() throws IOException {
		given: 'an insert document oplog entry'
			String document = getDocumentString(objId)

		when: 'it writes the document'
			timestampWriter.write(document, 0, document.length())

		then: 'destination should have the expected timestamp'
			timestampWriter.getTimestamp() == ('{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }')
	}

	def delegatesWritesToTargetWriter() throws IOException {
		given: 'an insert document oplog entry'
			String document = getDocumentString(objId)

		when: 'it writes the document'
			timestampWriter.write(document, 0, document.length())

		then: 'the delegate writer should write the document'
			1 * mockTargetWriter.append(document, 0, document.length())
	}

	def writesTimestampOfLastDocumentReadToDestination() throws IOException {
		given: 'two insert document oplog entries'
			String documentOne = getDocumentString(objId)
			String documentTwo = getDocumentString(anotherObjId)

		and: 'document one is already written'
			timestampWriter.write(documentOne, 0, documentOne.length())

		when: 'it writes document two'
			timestampWriter.write(documentTwo, 0, documentTwo.length())

		then: 'destination should have timestamp of document two'
			timestampWriter.getTimestamp() == ('{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }')
	}

	def doesNotWriteTimestampWhenDelegateWriterFails() throws IOException {
		given: 'two insert document oplog entries'
			String documentOne = getDocumentString(objId)
			String documentTwo = getDocumentString(anotherObjId)

		and: 'document one is already written'
			timestampWriter.write(documentOne, 0, documentOne.length())

		and: 'destination holds its timestamp'
			String lastRecordedTimestamp = timestampWriter.getTimestamp()

		and: 'delegate writer fails to write document two'
			mockTargetWriter.append(documentTwo, 0, documentTwo.length()) >> {throw new IOException("Disk Full")}

		when: 'it tries to write document two'
			timestampWriter.write(documentTwo, 0, documentTwo.length())

		then: 'destination should have timestamp of latest successful write'
			timestampWriter.getTimestamp() == lastRecordedTimestamp
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
			timestampWriter.write(document , 0, document.length())

		then: 'no timestamp should be written to destination'
			timestampWriter.getTimestamp() == ""
	}

}
