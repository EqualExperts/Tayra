package com.ee.beaver.io

import spock.lang.*
class CriteriaSpecs extends Specification{

	def documentOne = '{ "ts" : { "$ts" : 1357537752 , "$inc" : 1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	def documentTwo = '{ "ts" : { "$ts" : 1357537752 , "$inc" : 1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "person.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	def documentThree = '{ "ts" : { "$ts" : 1357213610 , "$inc" : 1} , "h" : 0 , "v" : 2 , "op" : "n" , "ns" : "" , "o" : { "msg" : "initiating set"}}'
	def criteria
	def filter = '-sDb=test'
	def returnsTrueIfCriteriaForASpecificDbIsSatisfied () {
		when:
			criteria = new Criteria (filter)
			
		then:
			criteria.isSatisfiedBy(documentOne)
	}
	
	def returnsFalseIfCriteriaForASpecificDbisNotSatisfied () {
		when:
			criteria = new Criteria (filter)
		
		then:
			! criteria.isSatisfiedBy(documentThree)
	}
}
