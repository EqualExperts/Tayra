package com.ee.tayra.io.criteria

import static com.ee.tayra.io.criteria.TimestampCriterion.*
import spock.lang.Specification

class TimestampCriteriaSpecs extends Specification {

  static def sinceTime = 'Since'
  static def untilTime = 'Until'

  def producesAppropriateTimestampCriteria () {
    given: 'a timestamp'
        def timestamp = '{ts:{$ts:1357537752,$inc:2}}'

    expect : 'instance created of type'
        klass == TimestampCriterion.create(timeLimit,timestamp).getClass()

    where : 'timelimit is as follows'
        timeLimit   |   klass
        sinceTime   |   TimestampCriterion.Since
        untilTime   |   TimestampCriterion.Until
    }
}
