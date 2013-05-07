package com.ee.tayra.io.criteria

import static com.ee.tayra.io.criteria.TimestampCriterion.*
import spock.lang.Specification

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
      criterion.getClass() == ExcludeCriterion
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
