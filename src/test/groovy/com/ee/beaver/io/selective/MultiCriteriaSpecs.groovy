package com.ee.beaver.io.selective

import spock.lang.Specification;

class MultiCriteriaSpecs  extends Specification {

	def document = '{ "ts" : { "$ts" : 1357537752 , "$inc" : 1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	MultiCriteria multiCriteria
	Criterion criterion1
	Criterion criterion2
	
	def setup() {
		criterion1 = Stub(Criterion)
		criterion2 = Stub(Criterion)
		List<Criterion> criteria = new ArrayList<Criterion>()
		criteria.add(criterion1)
		criteria.add(criterion2)
		multiCriteria = new MultiCriteria(criteria)
	}

	def returnsFalseIfDocumentDoesNotSatisfyAtLeastOneCriteria() {
		given:
			criterion1.isSatisfiedBy(document) >> true
			criterion2.isSatisfiedBy(document) >> false
		
		when:
			def isSatisfied = multiCriteria.isSatisfiedBy(document)
		
		then:
			! isSatisfied
	}

	def returnsTrueIfDocumentSatisfiesAllCriteria() {
		given:
			criterion1.isSatisfiedBy(document) >> true
			criterion2.isSatisfiedBy(document) >> true
		
		when:
			def isSatisfied = multiCriteria.isSatisfiedBy(document)
		
		then:
			isSatisfied
	}
}
