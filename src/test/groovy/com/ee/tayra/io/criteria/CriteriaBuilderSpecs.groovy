package com.ee.tayra.io.criteria

import spock.lang.Specification

class CriteriaBuilderSpecs extends Specification{

	CriteriaBuilder criteriaBuilder

	def setup() {
		criteriaBuilder = new CriteriaBuilder()
	}

	def producesNamespaceCriteria() {
		given: 'a namespace filter'
			def namespace = 'test'

		and: 'it is injected'
			criteriaBuilder.usingNamespace namespace

		when: 'criteria is built'
			def criterion = criteriaBuilder.build()

		then: 'Criterion should be an instance of NamespaceCriterion'
			criterion.criteria[0].getClass() == NamespaceCriterion
	}

	def producesTimestampCriteria() {
		given: 'timestamp filter'
			def timestamp = '{ts:{$ts:1357537752,$inc:1}}'
			criteriaBuilder.usingUntil timestamp

		when: 'criteria is built'
			def criterion = criteriaBuilder.build()

		then: 'Criterion should be an instance of TimestampCriterion'
			criterion.criteria()[0].getClass() == TimestampCriterion
	}

	def producesTimestampCriteriaUsingWithClosure() {
		given: 'timestamp filter'
			def timestamp = '{ts:{$ts:1357537752,$inc:1}}'

		when: 'criteria is built'
			def criterion = criteriaBuilder.build {
				usingUntil timestamp
			}

		then: 'Criterion should be an instance of DbCriteria'
			criterion.criteria()[0].getClass() == TimestampCriterion
	}

	def producesSinceCriteriaUsingWithClosure() {
		given: 'timestamp filter'
			def timestamp = '{ts:{$ts:1357537752,$inc:1}}'

		when: 'criteria is built'
			def criterion = criteriaBuilder.build {
				usingSince timestamp
			}

		then: 'Criterion should be an instance of SinceCriteria'
			criterion.criteria()[0].getClass() == SinceCriteria
	}

	def producesNamespaceCriteriaWithClosure() {
		given: 'namespace filter'
			def namespace = 'test'

		when: 'criteria is built'
			def criterion = criteriaBuilder.build {
				usingNamespace namespace
			}

		then: 'Criterion should be an instance of NamespaceCriterion'
			criterion.criteria()[0].getClass() == NamespaceCriterion
	}

	def producesExcludeCriteria() {
		given: 'a namespace filter'
			def namespace = 'test'

		when: 'criteria is built'
			def criterion = criteriaBuilder.build {
				usingNamespace namespace
				usingExclude()
			}

		then: 'Criterion returned should be an instance of sExclude'
			criterion.getClass() == SExcludeCriterion
	}

	def producesMultiCriteriaWithoutSExclude() {
		given: 'a namespace filter'
			def namespace = 'test'

		when: 'criteria is built'
			def criterion = criteriaBuilder.build {
				usingNamespace namespace
			}

		then: 'Criterion returned should be an instance of MultiCriteria'
			criterion.getClass() == MultiCriteria
	}
}
