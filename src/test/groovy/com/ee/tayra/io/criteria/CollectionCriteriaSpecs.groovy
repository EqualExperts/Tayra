package com.ee.tayra.io.criteria

import spock.lang.Specification;

class CollectionCriteriaSpecs extends Specification{
	static documentOne = '{ "ts" : { "$ts" : 1357537752 , "$inc" : 1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	static documentTwo = '{ "ts" : { "$ts" : 1357537752 , "$inc" : 1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.people" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	static configurationEntry = '{ "ts" : { "$ts" : 1358746349 , "$inc" : 1} , "h" : 0 , "v" : 2 , "op" : "n" , "ns" : "" , "o" : { "msg" : "initiating set"}}' 
	def collectionName = 'things'
	static criteria
	
	
	def satisfiesCollectionCriteriaOrNot () {
	  given:
	  		criteria = new CollectionCriteria(collectionName)
			  
	  expect:
	  		outcome == criteria.isSatisfiedBy(document)
	  
	  where:
	   		document             |   outcome
			documentOne          |   true
			documentTwo          |   false
			configurationEntry   |   false
	  
	}
}
