package com.ee.tayra.io.criteria

import static com.ee.tayra.io.criteria.TimestampCriterion.*
import spock.lang.Specification

public class TimestampSinceSpecs extends Specification{

  static def afterDocument = '{ts:{$ts:1357537755,$inc:1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
  static def beforeDocument = '{ts:{$ts:1357537750,$inc:1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'

  def satisfiesSinceCriteriaWithJSONTimestamp () {

    def timestamp = '{ts:{$ts:1357537752,$inc:2}}'
    def criteria = TimestampCriterion.create(SINCE_TIME,timestamp)

    expect:
      outcome == criteria.isSatisfiedBy(document)

    where:
      document                       | outcome
      afterDocument                  | true
      beforeDocument                 | false
  }

  def satisfiesSinceCriteriaWithISOTimestamp () {

    def timestamp = '2013-01-07T11:19:12Z'
    def criteria = TimestampCriterion.create(SINCE_TIME,timestamp)

    expect:
      outcome == criteria.isSatisfiedBy(document)

    where:
      document                       | outcome
      afterDocument                  | true
      beforeDocument                 | false
  }

  def shoutsWhenInvalidJSONTimestampFormatIsGiven() {
    given:'an invalid JSON timestamp'
      def invalidTimeStamp = '{ts:{$s:1357801207,$inc:1}}'

    when: 'timestamp criteria is applied to the document'
      def criteria = TimestampCriterion.create(SINCE_TIME,invalidTimeStamp)
    then: 'it shouts'
      thrown RuntimeException
  }

  def shoutsWhenInvalidISOTimestampFormatIsGiven() {
    given:'an invalid ISO timestamp'
      def invalidTimeStamp = '2012-12-2615:19:40Z'

    when: 'timestamp criteria is applied to the document'
      def criteria = TimestampCriterion.create(SINCE_TIME,invalidTimeStamp)
    then: 'it shouts'
      thrown RuntimeException
  }

  def shoutsWhenDocumentHasInvalidTimestampFormat() {
    given:'A timestamp'
      def timeStamp = '{ts:{$ts:1357801207,$inc:1}}'

    and: 'an oplog document with an invalid timestamp'
      def document = '{ts:{$s:1357801207,$inc:1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'

    and: 'a timestamp criteria'
      def criteria = TimestampCriterion.create(SINCE_TIME,timeStamp)
    when: 'timestamp criteria is applied to the document'
      criteria.isSatisfiedBy(document)

    then: 'it shouts'
      thrown RuntimeException
  }
}
