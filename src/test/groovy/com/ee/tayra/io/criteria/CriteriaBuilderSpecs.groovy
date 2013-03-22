package com.ee.tayra.io.criteria

import spock.lang.Specification
import static TimestampCriterion.*

class CriteriaBuilderSpecs extends Specification{

	CriteriaBuilder criteriaBuilder
	def setup() {
		MultiCriteria.metaClass.has = { instance ->
			delegate.criteria.find {
				it.getClass() == instance.getClass()
			}
		}
		criteriaBuilder = new CriteriaBuilder()
	}

	def producesNamespaceCriteria() {
		given: 'a namespace filter'
			def namespace = 'test'

		and: 'it is injected'
			criteriaBuilder.usingNamespace namespace

		when: 'criteria is built'
			def criterion = criteriaBuilder.build()

		then: 'Criterion should be an instance of NamespaceCriteria'
			criterion.criteria[0].getClass() == NamespaceCriteria
	}

	def producesTimestampCriteria() {
		given: 'timestamp filter'
			def timestamp = '{ts:{$ts:1357537752,$inc:1}}'
			criteriaBuilder.usingUntil timestamp

		when: 'criteria is built'
			def criterion = criteriaBuilder.build()

		then: 'Criterion should be an instance of TimestampCriteria'
			criterion.criteria()[0].getClass() == TimestampUntil
	}

	def producesTimestampCriteriaUsingWithClosure() {
		given: 'timestamp filter'
			def timestamp = '{ts:{$ts:1357537752,$inc:1}}'
			
		when: 'criteria is built'
			def criterion = criteriaBuilder.build {
				usingUntil timestamp
			}

		then: 'Criterion should be an instance of TimestampCriterion'
			criterion.has(create(UNTIL_TIME, timestamp))
	}

	def producesSinceCriteriaUsingWithClosure() {
		given: 'timestamp filter'
			def timestamp = '{ts:{$ts:1357537752,$inc:1}}'

		when: 'criteria is built'
			def criterion = criteriaBuilder.build {
				usingSince timestamp
			}

		then: 'Criterion should be an instance of SinceCriteria'
			criterion.has(create(SINCE_TIME, timestamp))
	}

	def producesNamespaceCriteriaWithClosure() {
		given: 'namespace filter'
			def namespace = 'test'

		when: 'criteria is built'
			def criterion = criteriaBuilder.build {
				usingNamespace namespace
			}

		then: 'Criterion should be an instance of NamespaceCriterion'
			criterion.has(new NamespaceCriterion(''))
	}

	def producesExcludeCriteriaWithClosure() {
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
