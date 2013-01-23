 package com.ee.tayra.domain.operation

import com.ee.tayra.domain.operation.DropIndex;
import com.ee.tayra.domain.operation.DropIndexFailed;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

class DropIndexSpecs extends RequiresMongoConnection{
 
 def collectionName = 'toys' 
 def operation
 def setup() {  
  operation = new DropIndex()
 }
 
 def assertThatIndexIsNotPresent(spec) {
  def dropped = true
  List<DBObject> indices = standalone.getDB(dbName).getCollection(collectionName).getIndexInfo()
   for ( DBObject index : indices) {
    if (index.get('key').toString() == spec.get('index').toString()){
     dropped = false;
    }
   }
  return dropped
 }

 def dropsASimpleIndex() {
  def key = new BasicDBObjectBuilder()
     .start()
      .add('roll', 1)
     .get()
  standalone.getDB(dbName).getCollection(collectionName).ensureIndex(key)
  
  given: 'drop index oplog entry'
   DBObject spec = new BasicDBObjectBuilder()
      .start()
       .add('deleteIndexes', collectionName)
       .push('index')
        .add('roll', new Double(1.0))
       .pop()
      .get()
      
  when: 'Operation runs'
   operation.doExecute(standalone.getDB(dbName), spec)
  
  then: 'Index should not be present'
    assertThatIndexIsNotPresent(spec)
 }
 
 def dropsACompoundIndex() {
  def key = new BasicDBObjectBuilder()
     .start()
      .add('roll', 1)
      .add('name', 1)
     .get()
  standalone.getDB(dbName).getCollection(collectionName).ensureIndex(key)
  
  given: 'drop index oplog entry for a compound index'
   DBObject spec = new BasicDBObjectBuilder()
      .start()
       .add('deleteIndexes', collectionName)
       .push('index')
         .add('roll', new Double(1.0))
        .add('name', new Double(1.0))
       .pop()
      .get()
      
  when: 'Operation runs'
   operation.doExecute(standalone.getDB(dbName), spec)
   
  then: 'Index should not be present'
   assertThatIndexIsNotPresent(spec)
 }
 
 def dropsAnIndexOnNestedField() {
  def key = new BasicDBObjectBuilder()
     .start()
      .add('name.fname', 1)      
     .get()
  standalone.getDB(dbName).getCollection(collectionName).ensureIndex(key)
  
  given: 'drop index oplog entry for a compound index'
   DBObject spec = new BasicDBObjectBuilder()
      .start()
       .add('deleteIndexes', collectionName)
       .push('index')
        .add('name.fname', new Double(1.0))
       .pop()
      .get()
      
  when: 'Operation runs'
   operation.doExecute(standalone.getDB(dbName), spec)
   
  then: 'Index should not be present'
   assertThatIndexIsNotPresent(spec)
 }
 
 
 def shoutsWhenIndexToBeDroppedDoesNotExist() throws Exception {
  given: 'a drop index oplog entry payload for a non-existent index'
   DBObject spec = new BasicDBObjectBuilder()
      .start()
       .add('deleteIndexes', collectionName)
       .push('index')
        .add('roll', new Double(1.0))
       .pop()
      .get()
  
  when: 'the operation runs'
   operation.doExecute(standalone.getDB(dbName), spec)
   
  then: 'it complains that index to be dropped does not exist'
   def problem = thrown(DropIndexFailed)
   problem.message == "Cannot drop index : " + spec.get('index').toString() + " Index doesn't exist."
 }

 def dropsAllIndexes() {
  given: 'an all drop index oplog entry '
   DBObject spec = new BasicDBObjectBuilder()
   .start()
    .add('deleteIndexes', collectionName)
    .add('index', '*')
   .get()
   
  when: 'the operation runs'
   operation.doExecute(standalone.getDB(dbName), spec)
  
  then: 'all indexes should be dropped'
   standalone.getDB(dbName).getCollection(collectionName).getIndexInfo().size() == 1
  
 }

 def dropsASimpleIndexBeforeVersion2_2_1() {
	 def key = new BasicDBObjectBuilder()
		.start()
		 .add('roll', 1)
		.get()
	 standalone.getDB(dbName).getCollection(collectionName).ensureIndex(key)
	 
	 given: 'drop index oplog entry'
	  DBObject spec = new BasicDBObjectBuilder()
		 .start()
		  .add('deleteIndexes', collectionName)
	      .add('index', 'roll_1')
		 .get()
		 
	 when: 'Operation runs'
	  operation.doExecute(standalone.getDB(dbName), spec)
	 
	 then: 'Index should not be present'
	   assertThatIndexIsNotPresent(spec)
	}
 
 def shoutsWhenASimpleIndexBeforeVersion2_2_1_DoesNotExist() {
	 given: 'drop index oplog entry'
		  DBObject spec = new BasicDBObjectBuilder()
			 .start()
			  .add('deleteIndexes', collectionName)
			  .add('index', 'roll_1')
		 .get()
		 
	 when: 'Operation runs'
	  		operation.doExecute(standalone.getDB(dbName), spec)
	 
	 then: 'it complains that index to be dropped does not exist'
	 	def problem = thrown(DropIndexFailed)
		 problem.message == "Cannot drop index : " + spec.get('index').toString() + " Index doesn't exist."
	}

}