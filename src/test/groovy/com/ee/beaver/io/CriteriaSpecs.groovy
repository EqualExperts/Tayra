package com.ee.beaver.io

import spock.lang.Specification;

class CriteriaSpecs extends Specification{

	def producesDbCriteria() {
		given: 'Selective database filter'
				def filter='-sDb=test'
				
		and: 'Criteria is formed with the above filter'
				def criteria = new Criteria(filter)
				
		when: 'Criteria gets the criterion'
				def criterion = criteria.getCriterion()
				
		then: 'Criterion should be an instance of DbCriteria'
				criterion instanceof DbCriteria
	}
	
	def producesAll() {
		given: 'No filter'
				def filter=''
		
		and: 'Criteria is formed with the above filter'
				def criteria = new Criteria(filter)
				
		when: 'Criteria gets the criterion'
			 	def criterion = criteria.getCriterion()
				 
		then:'Criterion should be an instance of All'
				criterion == Criterion.ALL
	}
	
	def producesTimestampCriteria() {
		given: 'time stamp filter'
				def filter='-sUntil={ts:{$ts:1357537752,$inc:1}}'
				
		and: 'Criteria is formed with the above filter'
				def criteria = new Criteria(filter)
				
		when: 'Criteria gets the criterion'
				def criterion = criteria.getCriterion()
				
		then: 'Criterion should be an instance of DbCriteria'
				criterion instanceof TimestampCriteria
	}
}