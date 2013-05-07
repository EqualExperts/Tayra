package com.ee.tayra.io.listener.timestamp

import org.bson.types.BSONTimestamp

import spock.lang.*

import com.ee.tayra.domain.operation.DocumentBuilder
import com.mongodb.BasicDBObjectBuilder

public class TimestampRecorderSpecs extends Specification {

    private TimestampRepository mockRepository
    def timestampRecorder
    private final String timestamp = '{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }'

  def setup() {
      mockRepository = Mock(TimestampRepository)
      mockRepository.retrieve() >> { timestamp }
      timestampRecorder = new TimestampRecorder(mockRepository)
  }

  def getDocumentString(def tstamp = 1352105652) {
        new DocumentBuilder(
                ts: new BSONTimestamp(tstamp, 1),
                h :'3493050463814977392',
                op :'c',
                ns : 'tayra.$cmd',
                o : new BasicDBObjectBuilder()
                        .start()
                        .add('create', 'home')
                        .get()
        ) as String
  }

    def savesTimestamp() {
      given: 'an insert document oplog entry'
        String document = getDocumentString()

      and: 'document is already written'
        timestampRecorder.onReadSuccess(document)
        timestampRecorder.onWriteSuccess(document)

      when: 'stop is called...'
        timestampRecorder.stop()

      then:
        1 * mockRepository.save(timestamp)
    }

    def recordsDocumentTimestampOnReadSuccess() {
      given: 'an insert document oplog entry'
        String document = getDocumentString()

     when: 'a successful read has occurred'
        timestampRecorder.onReadSuccess(document)

     then: 'recorder should have the timestamp of the document'
        timestampRecorder.getDocumentTimestamp() == timestamp
    }

    def preservesLastDocumentTimestampOnReadSuccess() {
      given: 'an insert document oplog entry'
        String document = getDocumentString(1352105653)

      when: 'a successful read has occurred'
        timestampRecorder.onReadSuccess(document)

      then: 'recorder should preserve timestamp of the last document'
        timestampRecorder.getLastDocumentTimestamp() == timestamp
    }

    def preservesDocumentTimestampsOnReadFailure() {
      given: 'an insert document oplog entry'
        String document = getDocumentString(1352105652)

      and: 'a document was already written'
        timestampRecorder.onReadSuccess(document)
        timestampRecorder.onWriteSuccess(document)

      when: 'a read failure has occurred'
        timestampRecorder.onReadFailure(null, new IOException("Disk Full!"))

      then: 'recorder should have timestamp of the previous document'
        timestampRecorder.getDocumentTimestamp() == '{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }'

      and: 'recorder should preserve timestamp of the last document'
        timestampRecorder.getLastDocumentTimestamp() == '{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }'
    }

    def preservesDocumentTimestampOnWriteSuccess() {
    given: 'an insert document oplog entry'
      String document = getDocumentString()

      and: 'a successful read has already occurred'
        timestampRecorder.onReadSuccess(document)

      when: 'a successful write notification is received'
        timestampRecorder.onWriteSuccess(document)

      then: 'recorder should preserve timestamp of the current document'
        timestampRecorder.getDocumentTimestamp() == timestamp
  }

    def updatesLastDocumentTimestampOnWriteSuccess() {
      given: 'an insert document oplog entry'
        String document = getDocumentString()

      and: 'a successful read has already occurred'
        timestampRecorder.onReadSuccess(document)

      when: 'a successful write notification is received'
        timestampRecorder.onWriteSuccess(document)

      then: 'recorder should update last timestamp with timestamp of current document '
        timestampRecorder.getLastDocumentTimestamp() == timestamp
    }

    def preservesLastDocumentTimestampOnWriteFailure() throws IOException {
    given: 'two insert document oplog entries'
      String documentOne = getDocumentString(1352105652)
      String documentTwo = getDocumentString(1352105653)

      and: 'document one is already written'
      timestampRecorder.onReadSuccess(documentOne)
      timestampRecorder.onWriteSuccess(documentOne)

      and: 'document two is read successfully'
        timestampRecorder.onReadSuccess(documentTwo)

      when: 'it receives write failure notification for document two'
        timestampRecorder.onWriteFailure(documentTwo, new IOException("Disk Full"))

      then: 'recorder should preserve timestamp of the current document'
        timestampRecorder.getDocumentTimestamp() == '{ "ts":"{ \\"$ts\\" : 1352105653 , \\"$inc\\" : 1} }'

      and: 'recorder should preserve timestamp of latest successful write'
      timestampRecorder.getLastDocumentTimestamp() == '{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }'
  }

  def preservesDocumentTimestampsWhenIncomingDocumentDoesNotHaveTimestampEntry() {
    given: 'a document without timestamp'
        String documentOne = getDocumentString(1352105652)
    String documentWithoutTS = new BasicDBObjectBuilder()
              .start()
                .add("name", "test")
              .get()
              .toString()

    and: 'document one was already written successfully'
    timestampRecorder.onReadSuccess(documentOne)
        timestampRecorder.onWriteSuccess(documentOne)

      and: 'document without timestamp is read successfully'
        timestampRecorder.onReadSuccess(documentWithoutTS)

      when: 'it receives notification for successful write for document without timestamp'
        timestampRecorder.onWriteSuccess(documentWithoutTS)

      then: 'recorder ignores recording document without timestamp'
        timestampRecorder.getDocumentTimestamp() == '{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }'

      and: 'recorder preserves timestamp of last document containing timestamp'
    timestampRecorder.getLastDocumentTimestamp() == '{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }'
  }

    def doesNothingOnReadStart() {
      given: 'an insert document oplog entry'
        String documentOne = getDocumentString(1352105652)
        String documentTwo = getDocumentString(1352105653)

      and: 'a document was already written'
        timestampRecorder.onReadSuccess(documentOne)
        timestampRecorder.onWriteSuccess(documentOne)

      when: 'a read start notification is received'
        timestampRecorder.onReadStart(documentTwo)

      then: 'recorder should preserve timestamp of the current document'
        timestampRecorder.getDocumentTimestamp() == '{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }'

      and: 'recorder should preserve timestamp of last document'
        timestampRecorder.getLastDocumentTimestamp() == '{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }'
    }

    def doesNothingOnWriteStart() {
      given: 'an insert document oplog entry'
        String documentOne = getDocumentString(1352105652)
        String documentTwo = getDocumentString(1352105653)

      and: 'a document was already written'
        timestampRecorder.onReadSuccess(documentOne)
        timestampRecorder.onWriteSuccess(documentOne)

      and: 'second document was read successfully'
        timestampRecorder.onReadSuccess(documentTwo)

      when: 'a write start notification is received'
        timestampRecorder.onWriteStart(documentTwo)

      then: 'recorder should preserve timestamp of the current document'
        timestampRecorder.getDocumentTimestamp() == '{ "ts":"{ \\"$ts\\" : 1352105653 , \\"$inc\\" : 1} }'

      and: 'recorder should preserve timestamp of last document'
        timestampRecorder.getLastDocumentTimestamp() == '{ "ts":"{ \\"$ts\\" : 1352105652 , \\"$inc\\" : 1} }'
    }
}
