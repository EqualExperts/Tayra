package com.ee.tayra.io.criteria

import com.ee.tayra.io.criteria.CriteriaBuilder
import com.ee.tayra.io.criteria.Criterion
import com.ee.tayra.io.criteria.NamespaceCriteria
import com.ee.tayra.io.criteria.TimestampCriteria

import spock.lang.Specification;

class CriteriaBuilderSpecs extends Specification{

	def criteriaBuilder

	def setup() {
		criteriaBuilder = new CriteriaBuilder()
	}

	def producesNamespaceCriteria() {
		given: 'a namespace filter'
			def namespace = 'test'

		and: 'it is injected'
			criteriaBuilder.usingNamespace namespace, false

		when: 'criteria is built'
			def criterion = criteriaBuilder.build()

		then: 'Criterion should be an instance of NamespaceCriteria'
			criterion.criteria()[0].getClass() == NamespaceCriteria
	}

	def producesTimestampCriteria() {
		given: 'timestamp filter'
			def timestamp = '{ts:{$ts:1357537752,$inc:1}}'
			criteriaBuilder.usingUntil timestamp, false

		when: 'criteria is built'
			def criterion = criteriaBuilder.build()

		then: 'Criterion should be an instance of TimestampCriteria'
			criterion.criteria()[0].getClass() == TimestampCriteria
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
				usingUntil timestamp, false
	        }
		then: 'Criterion should be an instance of DbCriteria'
			criterion.criteria()[0].getClass() == TimestampCriteria
	}

	def producesNamespaceCriteriaWithClosure() {
		given: 'namespace filter'
			def namespace = 'test'

		when: 'criteria is built'
			def criterion = criteriaBuilder.build {
				usingNamespace namespace, false
			}

		then: 'Criterion should be an instance of NamespaceCriteria'
			criterion.criteria()[0].getClass() == NamespaceCriteria
	}
}
