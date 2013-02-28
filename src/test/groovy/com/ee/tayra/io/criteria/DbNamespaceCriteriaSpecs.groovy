package com.ee.tayra.io.criteria

import spock.lang.Specification;

class DbNamespaceCriteriaSpecs extends Specification{
	
	static eelabsCreateCollectionEntry = '{ "ts" : Timestamp(1360665338000, 1), "h" : NumberLong("7391500989066642719"), "v" : 2, "op" : "c", "ns" : "eelabs.$cmd", "o" : { "create" : "countries", "capped" : null, "size" : null } }'
	static eelabsCreateCollectionWithInsert= '''
   "ns" : "eelabs.countries",
   "o" : { "_id" : ObjectId("511499dd9365898be4b00b0d"), "name" : "Test1" } }
'''
	static eelabsUpdateDoc = '"ns" : "eelabs.countries","o2" : { "_id" : ObjectId("511499dd9365898be4b00b0d") },"o" : { "$set" : { "name" : "Test2" } } }'
	static eelabsInsertDoc = '"ns" : "eelabs.countries" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	static eelabsDeleteDoc ='"ns" : "eelabs.countries", "b" : true, "o" : { "_id" : ObjectId("51149b949365898be4b00b0e") } }'
	static eelabsDropDatabase ='"ns" : "eelabs.$cmd", "o" : { "dropDatabase" : 1 } }'
	static eelabsInsertDocFail = '"ns" : "eelabs.friends" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	static eeDropIndexFail='"ns" : "ee.$cmd", "o" : { "deleteIndexes" : "countries", "index" : { "roll" : 1 } } }'
	static eelabsCreateCollection = '{ "ts" : { "$ts" : 1360685980 , "$inc" : 1} , "h" : -3549987509950246055 , "v" : 2 , "op" : "c" , "ns" : "eelabs.$cmd" , "o" : { "create" : "countries"}}'
	static eelabsDropCollection = '{ "ts" : { "$ts" : 1360688427 , "$inc" : 1} , "h" : 7867124016255307633 , "v" : 2 , "op" : "c" , "ns" : "eelabs.$cmd" , "o" : { "drop" : "countries"}}'
	static eelabsCreateIndex = '{ "ts" : { "$ts" : 1360732964 , "$inc" : 1} , "h" : -3247792282971197891 , "v" : 2 , "op" : "i" , "ns" : "eelabs.system.indexes" , "o" : { "_id" : { "$oid" : "511b232461ad583bb301e9ec"} , "ns" : "eelabs.countries" , "key" : { "name" : 1.0} , "name" : "name_1"}}'
	static eelabsDropIndex = '{ "ts" : { "$ts" : 1360733107 , "$inc" : 1} , "h" : 5409713632279739576 , "v" : 2 , "op" : "c" , "ns" : "eelabs.$cmd" , "o" : { "deleteIndexes" : "countries" , "index" : { "name" : 1.0}}}'
	static eelabsCappedCreateCollection=' { "ts" : { "$ts" : 1360731743 , "$inc" : 1} , "h" : 7090731753035073884 , "v" : 2 , "op" : "c" , "ns" : "eelabs.$cmd" , "o" : { "create" : "countries" , "capped" : true , "size" : 10000.0}}'
	static initiatingSet ='{ "ts" : Timestamp(1361164446000, 1), "h" : NumberLong(0), "v" : 2, "op" : "n", "ns" : "", "o" : { "msg" : "initiating set" } }'

	static tayraDocOne = '"ts" : { "$ts" : 1361881909 , "$inc" : 20} , "h" : -9075229313514241527 , "v" : 2 , "op" : "i" , "ns" : "tayra.people" , "o" : { "_id" : { "$oid" : "512cab35696006eb3408bfdb"} , "name" : 29.0}}'
	static tayraDocTwo = '{ "ts" : { "$ts" : 1361881909 , "$inc" : 21} , "h" : -4147590429930801744 , "v" : 2 , "op" : "i" , "ns" : "tayra.people" , "o" : { "_id" : { "$oid" : "512cab35696006eb3408bfdc"} , "name" : 30.0}}'
	static tayraDocThree = '{ "ts" : { "$ts" : 1361881909 , "$inc" : 22} , "h" : -6108520546793616506 , "v" : 2 , "op" : "i" , "ns" : "tayra.people" , "o" : { "_id" : { "$oid" : "512cab35696006eb3408bfdd"} , "name" : 31.0}}'
	
	static DLDocOne = '"ts" : { "$ts" : 1361881909 , "$inc" : 20} , "h" : -9075229313514241527 , "v" : 2 , "op" : "i" , "ns" : "DL.people" , "o" : { "_id" : { "$oid" : "512cab35696006eb3408bfdb"} , "name" : 29.0}}'
	static DLDocTwo = '{ "ts" : { "$ts" : 1361881909 , "$inc" : 21} , "h" : -4147590429930801744 , "v" : 2 , "op" : "i" , "ns" : "DL.people" , "o" : { "_id" : { "$oid" : "512cab35696006eb3408bfdc"} , "name" : 30.0}}'
	static DLDocThree = '{ "ts" : { "$ts" : 1361881909 , "$inc" : 22} , "h" : -6108520546793616506 , "v" : 2 , "op" : "i" , "ns" : "DL.people" , "o" : { "_id" : { "$oid" : "512cab35696006eb3408bfdd"} , "name" : 31.0}}'
	
	static eeDocOne = '"ts" : { "$ts" : 1361881909 , "$inc" : 20} , "h" : -9075229313514241527 , "v" : 2 , "op" : "i" , "ns" : "ee.people" , "o" : { "_id" : { "$oid" : "512cab35696006eb3408bfdb"} , "name" : 29.0}}'
	static eeDocTwo = '{ "ts" : { "$ts" : 1361881909 , "$inc" : 21} , "h" : -4147590429930801744 , "v" : 2 , "op" : "i" , "ns" : "ee.people" , "o" : { "_id" : { "$oid" : "512cab35696006eb3408bfdc"} , "name" : 30.0}}'
	static eeDocThree = '{ "ts" : { "$ts" : 1361881909 , "$inc" : 22} , "h" : -6108520546793616506 , "v" : 2 , "op" : "i" , "ns" : "ee.people" , "o" : { "_id" : { "$oid" : "512cab35696006eb3408bfdd"} , "name" : 31.0}}'
	
	def namespace ='eelabs'
	def multipleDBnamespace ='ee,tayra,DL'

	def criteria
	def document

  def satisfiesDatabaseCriteria(){
      criteria = new NamespaceCriteria (namespace, false)
	
    expect: 'criteria is satisfied for documents belonging to eelabs db'
      outcome == criteria.isSatisfiedBy(document)
	  
		where:
		document                       | outcome
	  eelabsCreateCollectionEntry      | true
	  eelabsCreateCollectionWithInsert | true
	  eelabsInsertDoc                  | true
	  eelabsUpdateDoc                  | true
	  eelabsDeleteDoc                  | true
	  eelabsDropCollection             | true
	  eelabsDropDatabase               | true
	  eelabsCreateIndex                | true
	  eelabsDropIndex                  | true
	  eelabsInsertDocFail              | true
  }

  def doesNotSatisfyDatabaseCriteria() {
		criteria = new NamespaceCriteria(namespace, false)

    expect: 'criteria is not satisfied for documents not belonging to eelabs db'
		outcome == criteria.isSatisfiedBy(document)

    where:
		document                       | outcome
      eeDropIndexFail                  | false
	  initiatingSet                    | false
  }
	
  def satisfiesDatabaseCriteriaWithSExclude(){
		criteria = new NamespaceCriteria (namespace, true)
	
    expect: 'criteria is not satisfied for documents belonging to eelabs db'
		outcome == criteria.isSatisfiedBy(document)
	
    where:
		  document                       | outcome
		eelabsCreateCollectionEntry      | false
		eelabsCreateCollectionWithInsert | false
		eelabsInsertDoc                  | false
		eelabsUpdateDoc                  | false
		eelabsDeleteDoc                  | false
		eelabsDropCollection             | false
		eelabsDropDatabase               | false
		eelabsCreateIndex                | false
		eelabsDropIndex                  | false
		eelabsInsertDocFail              | false
  }


  def doesNotSatisfyDatabaseCriteriaWithSExclude() {
		criteria = new NamespaceCriteria(namespace, true)

    expect: 'criteria is satisfied for documents belonging to other dbs than eelabs'
		outcome == criteria.isSatisfiedBy(document)

    where:
		document                   | outcome
		eeDropIndexFail                  | true
		initiatingSet                    | true
  }
	
  def satisfiesMultipleDatabaseCriteria(){
		criteria = new NamespaceCriteria (multipleDBnamespace, false)
	
    expect: 'criteria is satisfied for documents belonging to ee, tayra, DL dbs and not eelabs'
		  outcome == criteria.isSatisfiedBy(document)
	
    where:
		  document                       | outcome
		eelabsCreateCollectionEntry      | false
		eelabsCreateCollectionWithInsert | false
		eelabsInsertDoc                  | false
		eelabsUpdateDoc                  | false
		eelabsDeleteDoc                  | false
		eelabsDropCollection             | false
		eelabsDropDatabase               | false
		eelabsCreateIndex                | false
		eelabsDropIndex                  | false
		eelabsInsertDocFail              | false
		initiatingSet                    | false
		eeDropIndexFail                  | true
		tayraDocOne                      | true
		tayraDocTwo                      | true
		tayraDocThree                    | true
		DLDocOne                         | true
		DLDocTwo                         | true
		DLDocThree                       | true
		eeDocOne                         | true
		eeDocTwo                         | true
		eeDocThree                       | true
  }
	
  def satisfiesMultipleDatabaseCriteriaWithSExclude(){
		criteria = new NamespaceCriteria (multipleDBnamespace, true)
	
    expect: 'criteria is not satisfied for documents belonging to ee, tayra, DL dbs and satisfied for eelabs'
		  outcome == criteria.isSatisfiedBy(document)
	
    where:
		  document                       | outcome
		eelabsCreateCollectionEntry      | true
		eelabsCreateCollectionWithInsert | true
		eelabsInsertDoc                  | true
		eelabsUpdateDoc                  | true
		eelabsDeleteDoc                  | true
		eelabsDropCollection             | true
		eelabsDropDatabase               | true
		eelabsCreateIndex                | true
		eelabsDropIndex                  | true
		eelabsInsertDocFail              | true
		initiatingSet                    | true
		eeDropIndexFail                  | false
		tayraDocOne                      | false
		tayraDocTwo                      | false
		tayraDocThree                    | false
		DLDocOne                         | false
		DLDocTwo                         | false
		DLDocThree                       | false
		eeDocOne                         | false
		eeDocTwo                         | false
		eeDocThree                       | false
  }
}
