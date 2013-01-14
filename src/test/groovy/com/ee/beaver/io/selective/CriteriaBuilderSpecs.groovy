package com.ee.beaver.io.selective

import com.ee.beaver.io.selective.CriteriaBuilder
import com.ee.beaver.io.selective.Criterion;
import com.ee.beaver.io.selective.DbCriteria;
import com.ee.beaver.io.selective.TimestampCriteria;

import spock.lang.Specification;

class CriteriaBuilderSpecs extends Specification{

	def criteriaBuilder
	
	def setup() {
		criteriaBuilder = new CriteriaBuilder()
	}

	def producesDbCriteria() {
		given: 'Selective database filter'
			def dbName = 'test'

		when: 'Criteria gets the criterion'
			def criterion = criteriaBuilder.withDatabase(dbName)
				
		then: 'Criterion should be an instance of DbCriteria'
			criteriaBuilder.criteria.getAt(0) instanceof DbCriteria
	}

	def producesTimestampCriteria() {
		given: 'time stamp filter'
			def timestamp = '{ts:{$ts:1357537752,$inc:1}}'
				
		when: 'Criteria gets the criterion'
			def criterion = criteriaBuilder.withUntil(timestamp)
				
		then: 'Criterion should be an instance of DbCriteria'
			criteriaBuilder.criteria.getAt(0) instanceof TimestampCriteria
	}

	def producesAll() {
		given: 'No filter'
			def filter=''
			
		when: 'Criteria gets the criterion'
			def criterion = criteriaBuilder.build()
			
		then:'Criterion should be an instance of All'
			criterion == Criterion.ALL
	}
}