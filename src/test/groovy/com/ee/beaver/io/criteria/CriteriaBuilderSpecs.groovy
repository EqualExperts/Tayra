package com.ee.beaver.io.criteria

import com.ee.beaver.io.criteria.Criterion;
import com.ee.beaver.io.criteria.DbCriteria;
import com.ee.beaver.io.criteria.TimestampCriteria;
import com.ee.beaver.io.criteria.CriteriaBuilder

import spock.lang.Specification;

class CriteriaBuilderSpecs extends Specification{

	def criteriaBuilder
	
	def setup() {
		criteriaBuilder = new CriteriaBuilder()
	}

	def producesDbCriteria() {
		given: 'a database filter'
			def dbName = 'test'
			
		and: 'it is injected'
			criteriaBuilder.database(dbName)

		when: 'criteria is built'
			def criterion = criteriaBuilder.build()
			
		then: 'Criterion should be an instance of DbCriteria'
			criterion.criteria()[0] instanceof DbCriteria
	}

	def producesTimestampCriteria() {
		given: 'timestamp filter'
			def timestamp = '{ts:{$ts:1357537752,$inc:1}}'
			criteriaBuilder.until(timestamp)
				
		when: 'criteria is built'
			def criterion = criteriaBuilder.build()
				
		then: 'Criterion should be an instance of TimestampCriteria'
			criterion.criteria()[0] instanceof TimestampCriteria
	}

	def producesAll() {
		given: 'No filter'
			def filter=''
			
		when: 'criteria is built'
			def criterion = criteriaBuilder.build()
			
		then:'Criterion should be an instance of All'
			criterion == Criterion.ALL
	}
	
	def producesTimestampCriteriaUsingWithClosure() {
		given: 'timestamp filter'
			def timestamp = '{ts:{$ts:1357537752,$inc:1}}'
			criteriaBuilder.using {
				until timestamp
			}
			
		when: 'criteria is built'
			def criterion = criteriaBuilder.build()
				
		then: 'Criterion should be an instance of DbCriteria'
			criterion.criteria()[0] instanceof TimestampCriteria
	}

	def producesDbCriteriaUsingWithClosure() {
		given: 'database filter'
			def dbName = 'test'
			criteriaBuilder.using {
				database dbName
			}
	
		when: 'criteria is built'
			def criterion = criteriaBuilder.build()
		
		then: 'Criterion should be an instance of DbCriteria'
			criterion.criteria()[0] instanceof DbCriteria
	}

}