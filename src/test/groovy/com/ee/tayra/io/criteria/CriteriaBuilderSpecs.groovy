package com.ee.tayra.io.criteria

import com.ee.tayra.io.criteria.CriteriaBuilder;
import com.ee.tayra.io.criteria.Criterion;
import com.ee.tayra.io.criteria.DbCriteria;
import com.ee.tayra.io.criteria.TimestampCriteria;

import spock.lang.Specification;

class CriteriaBuilderSpecs extends Specification{

	def criteriaBuilder

	def setup() {
		criteriaBuilder = new CriteriaBuilder()
	}

//	def producesDbCriteria() {
//		given: 'a database filter'
//			def dbName = 'test'
//
//		and: 'it is injected'
//			criteriaBuilder.usingDatabase(dbName)
//
//		when: 'criteria is built'
//			def criterion = criteriaBuilder.build()
//
//		then: 'Criterion should be an instance of DbCriteria'
//			criterion.criteria()[0] instanceof DbCriteria
//	}

	def producesTimestampCriteria() {
		given: 'timestamp filter'
			def timestamp = '{ts:{$ts:1357537752,$inc:1}}'
			criteriaBuilder.usingUntil(timestamp)

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

		when: 'criteria is built'
			def criterion = criteriaBuilder.build {
				usingUntil timestamp
	        }
		then: 'Criterion should be an instance of DbCriteria'
			criterion.criteria()[0] instanceof TimestampCriteria
	}
//    
//	def producesDbCriteriaUsingWithClosure() {
//		given: 'database filter'
//			def dbName = 'test'
//
//		when: 'criteria is built'
//			def criterion = criteriaBuilder.build {
//				usingDatabase dbName
//
//			}
//
//		then: 'Criterion should be an instance of DbCriteria'
//			criterion.criteria()[0] instanceof DbCriteria
//	}
	
	def producesNamespaceCriteriaWithClosure() {
		given: 'namespace filter'
			def namespace = 'test'

		when: 'criteria is built'
			def criterion = criteriaBuilder.build {
				usingNamespace namespace

			}

		then: 'Criterion should be an instance of NamespaceCriteria'
			criterion.criteria()[0] instanceof NamespaceCriteria
	}
	

}