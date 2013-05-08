package com.ee.tayra.domain.operation

import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DB
import com.mongodb.DBObject
import com.mongodb.MongoClient

class DropCollectionSpecs extends RequiresMongoConnection {

  def operation
  private String collectionName = 'home'
  private String cappedCollectionName = 'person'
  private String absentCollectionName = 'people'
  private DB db

  def setup() {
    operation = new DropCollection()
    db = standalone.getDB(dbName)
  }


  def dropsACollection() throws Exception {
    given: 'a collection exists in a database'
      givenACollection()

    and: 'a drop collection oplog entry payload'
      def builder = MongoUtils.dropCollection(dbName, collectionName)
      DBObject spec = builder.o

    when: 'the operation runs'
      operation.doExecute(db, spec)

    then: 'the collection should not exist'
      ! db.collectionExists(collectionName)
  }

  private givenACollection() {
    BasicDBObject dbobj = new BasicDBObject()
    dbobj.put("name", "test")
    db.createCollection(collectionName ,null)
    db.getCollection(collectionName).insert(dbobj)
  }


  def dropsACappedCollection() throws Exception {
    given: 'a capped collection exists in a database'
      givenACappedCollection(standalone, db)

    and: 'a drop collection oplog entry payload'
      def builder = MongoUtils.dropCollection(dbName, cappedCollectionName)
      DBObject spec = builder.o

    when: 'the operation runs'
      operation.doExecute(db, spec)

    then: 'the collection should not exist'
      ! db.collectionExists(cappedCollectionName)
  }

  private givenACappedCollection(MongoClient standalone, DB db) {
    DBObject options = new BasicDBObjectBuilder()
                .start()
                  .add('capped', true)
                  .add('size', 65536)
                  .add('max', 2048)
                .get()
    db.createCollection(cappedCollectionName,options)
  }


  def shoutsWhenCollectionToBeDroppedDoesNotExistInTarget() throws Exception {
    given: 'a drop collection oplog entry payload for a non-existent collection'
      def builder = MongoUtils.dropCollection(dbName, absentCollectionName)
      DBObject spec = builder.o

    when: 'the operation runs'
      operation.doExecute(db, spec)

    then: 'it complains that collection to be dropped does not exist'
      def problem = thrown(OperationFailed)
      problem.message == "Could Not Drop Collection people"
  }
}
