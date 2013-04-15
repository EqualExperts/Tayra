package com.ee.tayra.io

import com.ee.tayra.domain.operation.DocumentBuilder
import org.bson.types.BSONTimestamp
import org.bson.types.ObjectId

import spock.lang.*

import com.mongodb.BasicDBObjectBuilder

public class TimestampRecorderSpecs extends Specification {

	private TimestampRecorder timestampRecorder
	private String dbName = 'tayra'
	private String collectionName = 'home'
	private String name = '[Test Name]'
	def objId = new ObjectId()
	def anotherObjId = new ObjectId()
    private File mockTimestampFile
    private PrintWriter mockConsole
    private final String timestamp = '{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }'


	def setup() {
        mockTimestampFile = Mock(File)
        mockConsole = Mock(PrintWriter)
		timestampRecorder = new TimestampRecorder(mockTimestampFile, mockConsole)
	}

	def getDocumentString(ObjectId objId) {
        new DocumentBuilder(
                ts: new BSONTimestamp(1352105652, 1),
                h :'3493050463814977392',
                op :'c',
                ns : dbName + '.$cmd',
                o : new BasicDBObjectBuilder()
                        .start()
                        .add('create', collectionName)
                        .get()
        ) as String
	}

	def recordsTimestampOnWriteSuccess() throws IOException {
	  given: 'an insert document oplog entry'
	    String document = getDocumentString(objId)

      and: 'timestamp recorder is configured to return last timestamp as...'
         def timestampRecorder = new TimestampRecorder(mockTimestampFile, mockConsole) {
           @Override
           String readLastTimestamp() {
               timestamp
           }
         }

      and: 'a successful read has occurred'
        timestampRecorder.onReadSuccess(document)

      when: 'a successful write notification is received'
        timestampRecorder.onWriteSuccess(document)

      then: 'destination should have the expected timestamp'
        timestampRecorder.getTimestamp() == timestamp
	}

    @Ignore
	def delegatesWritesToTargetWriter() throws IOException {
		given: 'an insert document oplog entry'
			String document = getDocumentString(objId)

		when: 'it writes the document'
			timestampRecorder.writeDocument(document)

		then: 'the delegate writer should write the document'
			1 * mockTargetWriter.writeDocument(document)
	}
    @Ignore
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

//	def doesNotWriteTimestampWhenDelegateWriterFails() throws IOException {
//		given: 'two insert document oplog entries'
//			String documentOne = getDocumentString(objId)
//			String documentTwo = getDocumentString(anotherObjId)
//
//		and: 'document one is already written'
//			timestampRecorder.writeDocument(documentOne)
//
//		and: 'destination holds its timestamp'
//			String lastRecordedTimestamp = timestampRecorder.getTimestamp()
//
//		and: 'delegate writer fails to write document two'
//			mockTargetWriter.writeDocument(documentTwo) >> {throw new IOException("Disk Full")}
//
//		when: 'it tries to write document two'
//			timestampRecorder.writeDocument(documentTwo)
//
//		then: 'destination should have timestamp of latest successful write'
//			timestampRecorder.getTimestamp() == lastRecordedTimestamp
//			thrown(IOException)
//	}
    @Ignore
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
