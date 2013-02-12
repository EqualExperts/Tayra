package com.ee.tayra.io.criteria

import spock.lang.Specification;

class NamespaceCriteriaSpecs extends Specification{
  static createCollectionEntry = '"ns" : "eelabs.$cmd", "o" : { "create" : "countries", "capped" : null, "size" : null } }'
  static createCollectionWithInsert= """\
   '"ns" : "eelabs.countries",
   "o" : { "_id" : ObjectId("511499dd9365898be4b00b0d"), "name" : "Test1" } }'
"""
  static updateDoc =
  '"ns" : "eelabs.countries","o2" : { "_id" : ObjectId("511499dd9365898be4b00b0d") },"o" : { "$set" : { "name" : "Test2" } } }'
  static insertDoc = '"ns" : "eelabs.countries" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
  static deleteDoc ='"ns" : "eelabs.countries", "b" : true, "o" : { "_id" : ObjectId("51149b949365898be4b00b0e") } }'
  static dropCollection ='"ns" : "eelabs.$cmd", "o" : { "drop" : "countries" } }'
  static dropDatabase ='"ns" : "eelabs.$cmd", "o" : { "dropDatabase" : 1 } }'
  static createIndex="""\
  '"ns" : "eelabs.system.indexes",
  "o" : { "_id" : ObjectId("5114a8e99365898be4b00b11"),"ns" : "eelabs.countries", "key" : { "name" : 1 }, "name" : "name_1" } }'
"""
  static dropIndex='"ns" : "eelabs.$cmd", "o" : { "deleteIndexes" : "countries", "index" : { "roll" : 1 } } }'
  static insertDocFail = '"ns" : "eelabs.friends" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
  static dropIndexFail='"ns" : "ee.$cmd", "o" : { "deleteIndexes" : "countries", "index" : { "roll" : 1 } } }'
  
  def namespaceOne ='eelabs'
  def namespaceTwo ='eelabs.countries'
  
  def criteria
  def document
  
  def satisfiesDatabaseCriteria (){
    criteria = new NamespaceCriteria (namespaceOne)
  expect:
    outcome == criteria.isSatisfiedBy(document)
  
    where:
    document                 | outcome
  createCollectionEntry      | true
  createCollectionWithInsert | true  
  insertDoc                  | true
  updateDoc                  | true    
  deleteDoc                  | true
  dropCollection             | true
  dropDatabase               | true
  createIndex                | true
  dropIndex                  | true
  
  insertDocFail              | true
  dropIndexFail              | false
}
    
  def satisfiesDatabaseAndCollectionCriteria (){
    criteria = new NamespaceCriteria (namespaceTwo)
  expect:
    outcome == criteria.isSatisfiedBy(document)
  
  where:
  document                   | outcome
  createCollectionEntry      | true
  createCollectionWithInsert | true
  insertDoc                  | true
  updateDoc                  | true
  deleteDoc                  | true
  dropCollection             | true
  dropDatabase               | true
  createIndex                | true
  dropIndex                  | true
  
  insertDocFail              | false
  dropIndexFail              | false
}
  
}
