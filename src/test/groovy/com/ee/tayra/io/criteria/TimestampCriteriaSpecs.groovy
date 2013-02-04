package com.ee.tayra.io.criteria

import java.text.SimpleDateFormat

import com.ee.tayra.io.criteria.TimestampCriteria;

import spock.lang.Specification

class TimestampCriteriaSpecs extends Specification {
	static document = '{ts:{$ts:1357537752,$inc:1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	static timestampJsonOne = '{ts:{$ts:1357537752,$inc:2}}'
	static timestampJsonTwo = '{ts:{$ts:1300000000,$inc:1}}'
	static timestampIsoOne = '2013-01-08T15:19:40Z'
	static timestampIsoTwo = '2012-12-26T15:19:40Z'
	

	def satisfiesTimestampCriteriaOrNot () {
		given: 'A timestamp and an oplog document'
			TimestampCriteria criteria = new TimestampCriteria(timestamp)
		expect:
			outcome == criteria.isSatisfiedBy(document)
			
		where:
			timestamp        |     outcome
			timestampJsonOne     |     true
			timestampJsonTwo     |     false
			timestampIsoOne      |     true
			timestampIsoTwo      |     false
	}

}
