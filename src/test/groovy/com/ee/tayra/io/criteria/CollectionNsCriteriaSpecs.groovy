package com.ee.tayra.io.criteria

import spock.lang.Specification

class CollectionNsCriteriaSpecs extends Specification{
	static createCollectionEntry = '{ "ts" : Timestamp(1360665338000, 1), "h" : NumberLong("7391500989066642719"), "v" : 2, "op" : "c", "ns" : "eelabs.$cmd", "o" : { "create" : "countries", "capped" : null, "size" : null } }'
	static createCollectionWithInsert= '''
		"ns" : "eelabs.countries",
		"o" : { "_id" : ObjectId("511499dd9365898be4b00b0d"), "name" : "Test1" } }
		'''
	static updateDoc =
	'"ns" : "eelabs.countries","o2" : { "_id" : ObjectId("511499dd9365898be4b00b0d") },"o" : { "$set" : { "name" : "Test2" } } }'
	static insertDoc = '"ns" : "eelabs.countries" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	static deleteDoc ='"ns" : "eelabs.countries", "b" : true, "o" : { "_id" : ObjectId("51149b949365898be4b00b0e") } }'
	static dropDatabase ='"ns" : "eelabs.$cmd", "o" : { "dropDatabase" : 1 } }'
	static insertDocFail = '"ns" : "eelabs.friends" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	static dropIndexFail='"ns" : "ee.$cmd", "o" : { "deleteIndexes" : "countries", "index" : { "roll" : 1 } } }'
	
	static createCollection = '{ "ts" : { "$ts" : 1360685980 , "$inc" : 1} , "h" : -3549987509950246055 , "v" : 2 , "op" : "c" , "ns" : "eelabs.$cmd" , "o" : { "create" : "countries"}}'
	static dropCollection = '{ "ts" : { "$ts" : 1360688427 , "$inc" : 1} , "h" : 7867124016255307633 , "v" : 2 , "op" : "c" , "ns" : "eelabs.$cmd" , "o" : { "drop" : "countries"}}'
	static createIndex = '{ "ts" : { "$ts" : 1360732964 , "$inc" : 1} , "h" : -3247792282971197891 , "v" : 2 , "op" : "i" , "ns" : "eelabs.system.indexes" , "o" : { "_id" : { "$oid" : "511b232461ad583bb301e9ec"} , "ns" : "eelabs.countries" , "key" : { "name" : 1.0} , "name" : "name_1"}}'
	static dropIndex = '{ "ts" : { "$ts" : 1360733107 , "$inc" : 1} , "h" : 5409713632279739576 , "v" : 2 , "op" : "c" , "ns" : "eelabs.$cmd" , "o" : { "deleteIndexes" : "countries" , "index" : { "name" : 1.0}}}'
	static cappedCreateCollection=' { "ts" : { "$ts" : 1360731743 , "$inc" : 1} , "h" : 7090731753035073884 , "v" : 2 , "op" : "c" , "ns" : "eelabs.$cmd" , "o" : { "create" : "countries" , "capped" : true , "size" : 10000.0}}'
	static initiatingSet ='{ "ts" : Timestamp(1361164446000, 1), "h" : NumberLong(0), "v" : 2, "op" : "n", "ns" : "", "o" : { "msg" : "initiating set" } }'
	
	def namespace ='eelabs.countries'
	
	def criteria
	def document
	
	
	def satisfiesDatabaseAndCollectionCriteria (){
		criteria = new NamespaceCriteria (namespace)
	expect:
		outcome == criteria.isSatisfiedBy(document)
	
	where:
		document                   | outcome
		insertDoc                  | true
		updateDoc                  | true
		deleteDoc                  | true
		dropDatabase               | true
		createCollection           | true
		cappedCreateCollection     | true
		dropCollection             | true
		createIndex                | true
		dropIndex                  | true
	}
	
	def doesNotSatisfyDatabaseAndCollectionCriteria (){
		criteria = new NamespaceCriteria (namespace)
		expect:
			outcome == criteria.isSatisfiedBy(document)
		
		where:
		document                   | outcome
		insertDocFail              | false
		dropIndexFail              | false
		initiatingSet              | false
	}

}
