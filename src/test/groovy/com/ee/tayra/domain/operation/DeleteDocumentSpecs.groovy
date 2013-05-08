package com.ee.tayra.domain.operation

import org.bson.types.ObjectId

import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject

class DeleteDocumentSpecs extends RequiresMongoConnection {

  private String collectionName = 'home'
  private String prefixedCollectionName = 'home.test'
  private String anotherDb = 'mongoose'
  def operation
  def objId = new ObjectId('509754dd2862862d511f6b57')
  def name = '[Test Name]'
  def documentToBeDeleted

  def setup() {
    documentToBeDeleted = new BasicDBObjectBuilder()
                .start()
                  .add('_id', objId)
                  .add('name', name)
                .get()

    insertIn(dbName, collectionName, documentToBeDeleted)
    insertIn(dbName, prefixedCollectionName, documentToBeDeleted)
    insertIn(anotherDb, collectionName, documentToBeDeleted)
    operation = new DeleteDocument(standalone)
  }

  private insertIn (String db, String collectionName, BasicDBObject documentToBeDeleted) {
    standalone.getDB(db).getCollection(collectionName).insert(documentToBeDeleted)
  }

  def cleanup() {
    standalone.getDB(dbName).getCollection(collectionName).drop()
    standalone.getDB(dbName).getCollection(prefixedCollectionName).drop()
    standalone.getDB(anotherDb).dropDatabase()
  }

  private void assertThatDocumentIsNotPresentInCollection(String db, String collection, DBObject document) {
    standalone.getDB(db).getCollection(collection).findOne(document) == null
  }


  def deletesDocument() throws Exception {
    given: 'a delete document oplog entry'
      def o = new BasicDBObjectBuilder()
            .start()
              .add('_id', objId)
            .get()
      def document = MongoUtils.deleteDocument(dbName, collectionName,o) as DBObject

    when: 'the operation runs'
      operation.execute(document.toString())

    then: 'document should not be present'
      assertThatDocumentIsNotPresentInCollection(dbName, collectionName, documentToBeDeleted)
  }


  def deletesDocumentInAPrefixedCollection() throws Exception {
    given: 'a delete document oplog entry on a prefixed collection'
      def o = new BasicDBObjectBuilder()
            .start()
              .add('_id', objId)
            .get()
      def document = MongoUtils.deleteDocument(dbName, prefixedCollectionName,o) as DBObject

    when: 'the operation runs'
      operation.execute(document.toString())

    then: 'document should not be present'
      assertThatDocumentIsNotPresentInCollection(dbName, prefixedCollectionName, documentToBeDeleted)
  }


  def deletesDocumentInAnotherDatabase() throws Exception {
    given: 'a delete document oplog entry on another database'
      def o = new BasicDBObjectBuilder()
            .start()
              .add('_id', objId)
            .get()
      def document = MongoUtils.deleteDocument(anotherDb, collectionName,o) as DBObject

    when: 'the operation runs'
      operation.execute(document.toString())

    then: 'document should not be present'
      assertThatDocumentIsNotPresentInCollection(anotherDb, collectionName, documentToBeDeleted)
  }


  def deletesNestedDocument() {
    given: 'a delete document oplog entry for a nested document'
      def objId = new ObjectId('5097791244ae3e2f63ec6d32')
      def documentToBeDeleted = new BasicDBObjectBuilder()
                    .start()
                      .add('_id', objId)
                      .add('name', name)
                      .push('address')
                        .add('street', '[Some Street]')
                        .add('city', '[Some City]')
                        .add('country', '[CN]')
                      .pop()
                    .get()

      standalone.getDB(dbName).getCollection(collectionName).insert(documentToBeDeleted)

      def document = MongoUtils.deleteDocument(dbName, collectionName, documentToBeDeleted) as DBObject

    when: 'the operation runs'
      operation.execute(document.toString())

    then: 'document should not be present'
      assertThatDocumentIsNotPresentInCollection(dbName, collectionName, documentToBeDeleted)
  }


  def shoutsWhenDocumentToDeleteDoesNotExistInTarget() throws Exception {
    given: 'a delete document oplog entry for non-existing document'
      def absentObjId = new ObjectId('509e8839f91e1d01ec6dfb50')
      def o = new BasicDBObjectBuilder()
            .start()
              .add('_id', absentObjId)
            .get()
      def document = MongoUtils.deleteDocument(dbName, collectionName,o) as DBObject

    when: 'the operation runs'
        operation.execute(document.toString())

    then: 'it complains that document to be deleted does not exist'
      def problem = thrown(OperationFailed)
        problem.message == 'Document does not exist { \"_id\" : { \"$oid\" : \"'+ absentObjId +'\"}}'
  }
}
