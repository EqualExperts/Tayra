package com.ee.tayra.io.reader

import com.ee.tayra.domain.MongoCollection
import com.ee.tayra.domain.MongoCollectionIterator
import com.ee.tayra.domain.operation.MongoUtils
import com.ee.tayra.utils.StringDocumentWriter
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import com.mongodb.MongoException
import com.mongodb.util.JSON
import com.mongodb.util.JSONSerializers
import org.bson.types.ObjectId
import spock.lang.Specification

public class OplogReaderSpecs extends Specification {

  private MongoCollection mockOplogCollection
  private MongoCollectionIterator<String> mockOplogCollectionIterator
  private CollectionReader reader
  private String dbName = 'tayra'
  private String collectionName = 'home'
  private String name = '[Test Name]'
  private String fromTimestamp = '{"ts" : { "$ts" : 1354096315 , "$inc" : 10}}'
  def objId

  def setup() {
    mockOplogCollection = Stub(MongoCollection)
    mockOplogCollectionIterator = Stub(MongoCollectionIterator)
    mockOplogCollection.find(fromTimestamp, false) >> mockOplogCollectionIterator
    reader = new OplogReader(mockOplogCollection, fromTimestamp, false)
    objId = new ObjectId()
  }


  def readsACreateCollectionOperationDocument() {
    given: 'a create collection oplog entry'
      def document = MongoUtils.createCollection(dbName, collectionName) as String

    and: 'oplog iterator returns the document'
      mockOplogCollectionIterator.hasNext() >> true
      mockOplogCollectionIterator.next() >> document

    when: 'reader reads that document'
      String oplogDocumentString = reader.readDocument()

    then: 'it should read expected document'
      oplogDocumentString == '{ "ts" : { "$timestamp" : { "t" : 1352105652 , "i" : 1}} , "h" : "3493050463814977392" , "op" : "c" , "ns" : "' + dbName + '.$cmd" , "o" : { "create" : "' + collectionName + '" , "capped" : false , "size" :  null  , "max" :  null }}'
  }


  def readsAnInsertOperationDocument() {
    given: 'an insert document oplog entry'
      def o = new BasicDBObjectBuilder()
            .start()
              .add('_id', objId)
              .add('name', name)
            .get()
      def document = MongoUtils.insertDocument(dbName, collectionName, o) as String

    and: 'oplog iterator returns the document'
      mockOplogCollectionIterator.hasNext() >> true
      mockOplogCollectionIterator.next() >> document

    when: 'reader reads that document'
      String oplogDocumentString = reader.readDocument()

    then: 'it should read the expected document'
      oplogDocumentString == '{ "ts" : { "$timestamp" : { "t" : 1352105652 , "i" : 1}} , "h" : "3493050463814977392" , "op" : "i" , "ns" : "' + "$dbName.$collectionName" + '" , "o" : { "_id" : { "$oid" : "' + objId + '"} , "name" : "' + name + '"}}'
  }

    def readsBinaryData() {
      given: 'a binary data insert oplog entry'
        def o = new BasicDBObjectBuilder()
              .start()
                .add('_id', objId)
                .add('binaryData', binaryData)
              .get()
      def document = MongoUtils.insertDocument(dbName, collectionName, o) as String

      and: 'oplog iterator returns the document'
        mockOplogCollectionIterator.hasNext() >> true
        mockOplogCollectionIterator.next() >> document

      when: 'reader reads that document'
        String oplogDocumentString = reader.readDocument()

      then: 'it should read the expected document having binary data'
        oplogDocumentString == '{ "ts" : { "$timestamp" : { "t" : 1352105652 , "i" : 1}} , "h" : "3493050463814977392" , "op" : "i" , "ns" : "' + "$dbName.$collectionName" + '" , "o" : { "_id" : { "$oid" : "' + objId + '"} , "binaryData" : { "$binary" : "' + binaryDataAsString + '" , "$type" : 0}}}'
    }

  def readsAnUpdateOperationDocument() {
    given: 'an update document oplog entry'
      def o2 = new BasicDBObjectBuilder()
            .start()
              .add('_id', objId)
            .get()

      def o = new BasicDBObjectBuilder()
            .start()
              .add('name', name)
            .get()
      def document = MongoUtils.updateDocument(dbName, collectionName, o2, o) as String

    and: 'oplog iterator returns the document'
      mockOplogCollectionIterator.hasNext() >> true
      mockOplogCollectionIterator.next() >> document

    when: 'reader reads that document'
      String oplogDocumentString = reader.readDocument()

    then: 'it should read the expected document'
      oplogDocumentString == '{ "ts" : { "$timestamp" : { "t" : 1352105652 , "i" : 1}} , "h" : "3493050463814977392" , "op" : "u" , "ns" : "' + "$dbName.$collectionName" + '" , "o2" : { "_id" : { "$oid" : "' + objId + '"}} , "o" : { "name" : "' + name + '"}}'
  }


  def readsARemoveOperationDocument() {
    given: 'a delete document oplog entry'
      def o = new BasicDBObjectBuilder()
            .start()
              .add('_id', objId)
            .get()
      def document = MongoUtils.deleteDocument(dbName, collectionName,o) as String

    and: 'oplog iterator returns the document'
      mockOplogCollectionIterator.hasNext() >> true
      mockOplogCollectionIterator.next() >> document

    when:'reader reads that document'
      String oplogDocumentString = reader.readDocument()

    then:'it should read the expected document'
      oplogDocumentString == '{ "ts" : { "$timestamp" : { "t" : 1352105652 , "i" : 1}} , "h" : "3493050463814977392" , "op" : "d" , "ns" : "' + "$dbName.$collectionName" + '" , "b" : true , "o" : { "_id" : { "$oid" : "' + objId + '"}}}'
  }

  def readsADropCollectionOperationDocument() {
    given: 'a drop collection oplog entry'
      def document = MongoUtils.dropCollection(dbName, collectionName) as String

    and: 'oplog iterator returns the document'
      mockOplogCollectionIterator.hasNext() >> true
      mockOplogCollectionIterator.next() >> document

    when: 'reader reads that document'
      String oplogDocumentString = reader.readDocument()

    then: 'it should read the expected document'
      oplogDocumentString == '{ "ts" : { "$timestamp" : { "t" : 1352105652 , "i" : 1}} , "h" : "3493050463814977392" , "op" : "c" , "ns" : "' + "$dbName" +'.$cmd" , "o" : { "drop" : "'+ collectionName +'"}}'
  }


  def shoutsWhenQueryingForDocumentWithAClosedReader() {
    when: 'reader is closed'
      reader.close()

    and: 'reader tries to query for a document'
      reader.hasDocument()

    then: 'error message should be shown as'
      def problem = thrown(ReaderAlreadyClosed)
      problem.message == "Reader Already Closed"
  }


  def shoutsWhenFetchingForDocumentWithAClosedReader() {
    when: 'reader is closed'
      reader.close()

    and: 'reader tries to read a document'
      reader.readDocument()

    then: 'error message should be shown as'
      def problem = thrown(ReaderAlreadyClosed)
      problem.message == "Reader Already Closed"
  }

  def cannotCloseAnAlreadyClosedReader() {
    given: 'reader is closed'
      reader.close()

    when: 'reader is closed again'
      reader.close()

    then: 'error message should be shown as'
      def problem = thrown(ReaderAlreadyClosed)
      problem.message == "Reader Already Closed"
  }

    def notifiesWhenReadingADocumentFromOplogBegins()
    throws Exception {
      given: 'a read notifier'
        ReadNotifier mockNotifier = Mock(ReadNotifier)
        reader.notifier = mockNotifier

      and: 'oplog iterator signals availability of document'
        mockOplogCollectionIterator.hasNext() >> true

      when: 'the document availability is checked for'
        reader.hasDocument()

      then: 'it notifies start of read'
        1 * mockNotifier.notifyReadStart("")
    }

    def notifiesWhenReadingADocumentFromOplogIsSuccessful()
    throws Exception {
      given: 'a collection reader and a writer'
        ReadNotifier mockNotifier = Mock(ReadNotifier)
        reader.notifier = mockNotifier

      and: 'oplog iterator returns the document'
        def document = MongoUtils.createCollection(dbName, collectionName) as String
        mockOplogCollectionIterator.next() >> document

      when: 'the document is read'
        reader.readDocument()

      then: 'it notifies a successful read'
        1 * mockNotifier.notifyReadSuccess(document)
    }

    def notifiesWhenReadingDocumentFromOplogFails() throws Exception {
      given: 'a collection reader and a writer'
        ReadNotifier mockNotifier = Mock(ReadNotifier)
        reader.notifier = mockNotifier

      and: 'oplog iterator throws an exception'
        RuntimeException problem = new MongoException("Connection Lost!")
        mockOplogCollectionIterator.next() >> { throw problem }

      when: 'the document is read'
        reader.readDocument()

      then: 'it notifies a failed read'
        problem.getClass() == MongoException
        1 * mockNotifier.notifyReadFailure(null, problem)
    }

    def notifiesWhenIteratingOnOplogFails() throws Exception {
      given: 'a collection reader and a writer'
        ReadNotifier mockNotifier = Mock(ReadNotifier)
        reader.notifier = mockNotifier

      and: 'oplog iterator throws a exception on availability of next document'
        RuntimeException problem = new MongoException("Connection Lost!")
        mockOplogCollectionIterator.hasNext() >> { throw problem }

      when: 'the document availability is checked'
        reader.hasDocument()

      then: 'it notifies a failed read'
        problem.getClass() == MongoException
        1 * mockNotifier.notifyReadFailure(null, problem)
    }

    def binaryDataAsString = 'UmVhZE1lLnR4dAo9PT09PT09PT09CgpUYXlyYSBpcyBhbiBpbmNyZW1lbnRhbCBiYWNrdXAgYW5kIHJlc3RvcmUgdXRpbGl0eSBmb3IgTW9uZ29EQi4KCllvdSBjYW4gdmlldyBUYXlyYSBhcyBhbiBleHRlcm5hbCBhbmQgcGVyc2lzdGVudCBvcGxvZyB0aGF0IGlzIHN0b3JlZCBvbiB0aGUKZmlsZSBzeXN0ZW0gaW5zdGVhZCBvZiByZXNpZGluZyB3aXRoaW4gTW9uZ29EQi4gVGhlIGZpbGVzIGdlbmVyYXRlZCBjYW4gdGhlbiBiZQp1c2VkIHRvIHJlc3RvcmUgdGhlIGRhdGEgaW5jcmVtZW50YWxseSB0byBhbnkgdGFyZ2V0IE1vbmdvREIgaW5zdGFuY2UsIHdoaWNoIGNhbgpiZSBpbmplY3RlZCBpbnRvIGEgcmVwbGljYSBzZXQgaW4gY2FzZSBvZiBhbnkgZXZlbnQgdGhhdCB0aHJlYXRlbnMgdGhlCmF2YWlsYWJpbGl0eSBvZiBzZXJ2aWNlLgoKSW5pdGlhbGx5LCB5b3UgY2FuIHNlZWQgYmFja3VwIGFuZCBzdWJzZXF1ZW50bHkgYmFjay11cCBkYXRhIGluIGFuIGluY3JlbWVudGFsCmZhc2hpb24uCgpZb3UgY2FuIGFsc28gc3BlY2lmeSB0aGUgbnVtYmVyIG9mIGZpbGVzIGFuZCBzaXplIG9mIHRob3NlIGZpbGVzIHRvIGVuc3VyZQpyb3RhdGluZyBvdXRwdXQgZmlsZXMsIGdpdmluZyB5b3UgYSB3aW5kb3cgdG8gcGVyZm9ybSByZXN0b3JlIG9uY2UgbWF4IG51bWJlcgpvbiB0aGUgcm90YXRpbmcgZmlsZSBpcyByZWFjaGVkLgoKWW91IGNhbiBkZWNpZGUgZWl0aGVyIHRvIGJhY2t1cCBmcm9tIHByaW1hcnkgb3Igc2Vjb25kYXJ5IG9mIHRoZSByZXBsaWNhIHNldCBieQpzdGFydGluZyB0aGUgcHJvY2VzcyBvdmVyIHRoYXQgbm9kZS4gSW4gdGhlIGV2ZW50IG9mIGEgbm9kZSBjcmFzaCwgVGF5cmEgd2lsbAphdXRvbWF0aWNhbGx5IHN3aXRjaCBvdmVyIHRvIHRoZSBuZXh0IHNpbWlsYXIgbm9kZSAocHJpbWFyeSBmb3IgcHJpbWFyeSBhbmQKc2Vjb25kYXJ5IHByZWZlcnJlZCBmb3Igc2Vjb25kYXJ5KSBhbmQgd2lsbCByZXN1bWUgdGhlIGJhY2t1cCBmcm9tIHdoZXJlIGl0IGxlZnQKd2l0aG91dCB1c2VyIGludGVydmVudGlvbi4KCllvdSBjYW4gYWxzbyBwZXJmb3JtIGEgYmFja3VwIG9yIHJlc3RvcmUgYnkgc2VsZWN0aXZlbHkgaW5jbHVkaW5nIG9yIGV4Y2x1ZGluZyAKZG9jdW1lbnRzIHNhdGlzZnlpbmcgYSBjZXJ0YWluIGNyaXRlcmlhLiBDcml0ZXJpYSB3aGljaCBjYW4gYmUgYXBwbGllZCBhcmUgCnRpbWUtYm91bmRpbmcgZG9jdW1lbnRzLCBmaWx0ZXJpbmcgb24gdGhlIGJhc2lzIG9mIGRhdGFiYXNlIGFuZCBjb2xsZWN0aW9ucy4KCllvdSBjYW4gaGF2ZSBhIGRyeS1ydW4gdG8gYW5hbHlzZSB0aGUgZG9jdW1lbnRzIHlvdSBoYXZlIHNlbGVjdGVkIHRvIGJlIHJlc3RvcmVkLgoKRmVhdHVyZXMgU3VtbWFyeToKKiBTZWxlY3RpdmUgcmVzdG9yZQoqIFJvdGF0aW5nIExvZ3MgaW4gYmFja3VwIGFuZCByZXN0b3JlCiogU3Vydml2aW5nIG5vZGUgY3Jhc2ggaW4gYSByZXBsaWNhIHNldAoqIFNlY3VyZWQgYW5kIHVuc2VjdXJlZCBiYWNrdXAvcmVzdG9yZQoqIERyeS1ydW4KKiBTZWxlY3RpdmUgYmFja3VwCiogTW9yZSBncmFudWxhciBzZWxlY3Rpb24gY3JpdGVyaWEgZm9yIGJhY2t1cCBhbmQgcmVzdG9yZQogIC1tdWx0aXBsZSBkYnMgYW5kIGNvbGxlY3Rpb25zCiAgLWV4Y2x1ZGUgdGhlIGNyaXRlcmlhIGdpdmVuCgpQcmUtUmVxdWlzaXRlcwoqIEl0IGFzc3VtZXMgdGhhdCB5b3UgaGF2ZSBKREsxLjYgb3IgMS43IGluc3RhbGxlZCBhbmQgeW91IGhhdmUgamF2YQppbiB5b3VyIHBhdGguCgpSdW5uaW5nIFRheXJhCiogQWZ0ZXIgaGF2aW5nIGV4cGxvZGVkIHRoZSBUYXlyYSB6aXAgLQogICogRm9yIFVuaXggbWFjaGluZSwgZ3JhbnQgZXhlY3V0YWJsZSBwZXJtaXNzaW9ucyB0byBiYWNrdXAuc2ggYW5kIHJlc3RvcmUuc2gKICAgIHNjcmlwdHMuCiAgKiBJbiBvcmRlciB0byBzdGFydCBiYWNraW5nIHVwIGEgTW9uZ29EQiAocGFydGljaXBhdGluZyBpbiBhIFJlcGxpY2FTZXQpLCB5b3UKICAgIG1heSB1c2UgdGhlIGJhY2t1cCBzY3JpcHQuCiAgKiBJbiBvcmRlciB0byByZXN0b3JlIGEgYmFja2VkIHVwIGZpbGUgdG8gYSBNb25nb0RCIChjYW4gYmUgaW4gc3RhbmRhbG9uZQogIG1vZGUpLCB5b3UgbWF5IHVzZSB0aGUgcmVzdG9yZSBzY3JpcHQuCiogV2hlbiB1c2luZyByb3RhdGluZyBsb2dzIGZlYXR1cmUsIHRoZSBsb2cgd2l0aCBtYXhpbXVtIGluZGV4IHdpbGwgaGF2ZQogIGVudHJpZXMgZmFydGhlc3QgaW4gaGlzdG9yeS4KCldoYXQgbWF5IEZ1dHVyZSByZWxlYXNlcyBpbmNsdWRlPwoqIEZpbHRlciBvdXQgZG9jdW1lbnRzIG9uIHRoZSBiYXNpcyBvZiBvcGVyYXRpb24gcGVyZm9ybWVkCiogR2xvYnMgZm9yIGRhdGFiYXNlIGFuZCBjb2xsZWN0aW9uIG5hbWVzCg=='
    def binaryData = JSON.parse('{ "$binary" : "' + binaryDataAsString + '" , "$type" : 0}')
}
