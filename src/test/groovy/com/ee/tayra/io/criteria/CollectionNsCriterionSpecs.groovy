package com.ee.tayra.io.criteria

import spock.lang.Specification

class CollectionNsCriterionSpecs extends Specification {

  static eelabsCreateCollectionEntry = ''' "op" : "c", "ns" : "eelabs.$cmd",
        "o" : { "create" : "countries", "capped" : null, "size" : null } }'''
  static eelabsCreateCollectionWithInsert= '''"ns" : "eelabs.countries", "o" :
        { "_id" : ObjectId("511499dd9365898be4b00b0d"), "name" : "Test1" } }'''
  static eelabsUpdateDoc = ''' "op" : "u", "ns" : "eelabs.countries",
        "o2" :{ "_id" : ObjectId("511499dd9365898be4b00b0d") },
        "o" : { "$set" : { "name" : "Test2" } } }'''
  static eelabsInsertDoc = '''
          "op" : "i" , "ns" : "eelabs.countries" , "o" :
        { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'''
  static eelabsDeleteDoc = ''' "op" : "d" , "ns" : "eelabs.countries",
        "b" : true, "o" : { "_id" : ObjectId("51149b949365898be4b00b0e") } }'''
  static eelabsDropDatabase = ''' "op" : "c" , "ns" : "eelabs.$cmd",
        "o" : { "dropDatabase" : 1 } }'''
  static eelabsInsertDocFail = ''' "op" : "i" , "ns" : "eelabs.friends" , "o" :
        { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'''
  static eeDropIndexFail = ''' "op" : "c" , "ns" : "ee.$cmd",
        "o" : { "deleteIndexes" : "countries", "index" : { "roll" : 1 } } }'''
  static eelabsCreateCollection = ''' "op" : "c" , "ns" : "eelabs.$cmd" ,
        "o" : { "create" : "countries"}}'''
  static eelabsDropCollection = ''' "op" : "c" , "ns" : "eelabs.$cmd" ,
        "o" : { "drop" : "countries"}}'''
  static eelabsCreateIndex = ''' "op" : "i" , "ns" : "eelabs.system.indexes" ,
        "o" : { "_id" : { "$oid" : "511b232461ad583bb301e9ec"} , "ns" : "eelabs.countries" , "key" : { "name" : 1.0} , "name" : "name_1"}}'''
  static eelabsDropIndex = ''' "op" : "c" , "ns" : "eelabs.$cmd" ,
        "o" : { "deleteIndexes" : "countries" , "index" : { "name" : 1.0}}}'''
  static eelabsCappedCreateCollection=''' "op" : "c" , "ns" : "eelabs.$cmd" ,
        "o" : { "create" : "countries" , "capped" : true , "size" : 10000.0}}'''
  static initiatingSet = ''' "op" : "n" , "ns" : "",
        "o" : { "msg" : "initiating set" } }'''

  static tayraInsertOne = ''' "op" : "i" , "ns" : "tayra.people" , "o" :
        { "_id" : { "$oid" : "512cab35696006eb3408bfdb"} , "name" : 29.0}}'''
  static tayraInsertTwo = ''' "op" : "i" , "ns" : "tayra.people" , "o" :
        { "_id" : { "$oid" : "512cab35696006eb3408bfdc"} , "name" : 30.0}}'''
  static tayraInsertThree = ''' "op" : "i" , "ns" : "tayra.project" , "o" :
        { "_id" : { "$oid" : "512cab35696006eb3408bfdd"} , "name" : 31.0}}'''

  static eePrefixedInsertOne = ''' "op" : "i" , "ns" : "ee.people.addresses" , "o" :
        { "_id" : { "$oid" : "512cab35696006eb3408bfdb"} , "name" : 29.0}}'''
  static eePrefixedInsertTwo = ''' "op" : "i" , "ns" : "ee.people.addresses" , "o" :
        { "_id" : { "$oid" : "512cab35696006eb3408bfdc"} , "name" : 30.0}}'''
  static eeInsertThree = ''' "op" : "i" , "ns" : "ee.thing" , "o" :
        { "_id" : { "$oid" : "512cab35696006eb3408bfdd"} , "name" : 31.0}}'''

  def dBAndCollectionNamespace = 'eelabs.countries'
  def multipleDBAndCollectionNamespace = 'ee.people.addresses,eelabs.countries,tayra.project'

  def criteria
  def document

  def satisfiesDatabaseAndCollectionCriteria(){
    criteria = new NamespaceCriterion (dBAndCollectionNamespace)

    expect: '''criteria is satisfied for documents belonging to eelabs db
           and countries collection'''
      outcome == criteria.isSatisfiedBy(document)

    where:
      document                       | outcome
    eelabsInsertDoc                  | true
    eelabsUpdateDoc                  | true
    eelabsDeleteDoc                  | true
    eelabsDropDatabase               | true
    eelabsCreateCollection           | true
    eelabsCappedCreateCollection     | true
    eelabsDropCollection             | true
    eelabsCreateIndex                | true
    eelabsDropIndex                  | true
}

  def doesNotSatisfyDatabaseAndCollectionCriteria() {
    criteria = new NamespaceCriterion(dBAndCollectionNamespace)

    expect: '''criteria is not satisfied for documents belonging to other than
           eelabs db and countries collection'''
      outcome == criteria.isSatisfiedBy(document)

    where:
      document                     | outcome
    eelabsInsertDocFail            | false
    eeDropIndexFail                | false
    initiatingSet                  | false
  }

  def satisfiesMultipleDatabaseAndCollectionCriteria(){
    criteria = new NamespaceCriterion (multipleDBAndCollectionNamespace)

    expect: '''criteria is satisfied for documents belonging to ee.people.addresses,
        eelabs.countries, tayra.project collections and not others'''
    outcome == criteria.isSatisfiedBy(document)

    where:
      document                       | outcome
    eelabsCreateCollectionEntry      | true
    eelabsCreateCollectionWithInsert | true
    eelabsInsertDoc                  | true
    eelabsUpdateDoc                  | true
    eelabsDeleteDoc                  | true
    eelabsDropDatabase               | true
    eelabsCreateCollection           | true
    eelabsCappedCreateCollection     | true
    eelabsDropCollection             | true
    eelabsCreateIndex                | true
    eelabsDropIndex                  | true
    tayraInsertOne                   | false
    tayraInsertTwo                   | false
    tayraInsertThree                 | true
    eePrefixedInsertOne              | true
    eePrefixedInsertTwo              | true
    eeInsertThree                    | false
  }
}
