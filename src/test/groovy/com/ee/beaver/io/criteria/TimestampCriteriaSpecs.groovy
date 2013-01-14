package com.ee.beaver.io.criteria

import java.text.SimpleDateFormat

import com.ee.beaver.io.criteria.TimestampCriteria;

import spock.lang.Specification

class TimestampCriteriaSpecs extends Specification {

	def returnsTrueIfDocumentIsEarlierThanTimestamp () {
		
		given: 'A timestamp and an oplog document'
			def timeStamp = '{ts:{$ts:1357537752,$inc:2}}'
			def document = '{ts:{$ts:1357537752,$inc:1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
		
		when: 'timestamp criteria is applied to the document'
			TimestampCriteria criteria = new TimestampCriteria(timeStamp)
								
		then: 'it returns true if the document is older than the timestamp'
			criteria.isSatisfiedBy(document)
		
	}

	def returnsFalseIfDocumentIsLaterThanTimestamp () {
		
		given: 'A timestamp and an oplog document'
			def timeStamp = '{ts:{$ts:1300000000,$inc:1}}'
			def document = '{ts:{$ts:1357537752,$inc:1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
		
		when: 'timestamp criteria is applied to the document'
			TimestampCriteria criteria = new TimestampCriteria(timeStamp)
								
		then: 'it returns true if the document is older than the timestamp'
			! criteria.isSatisfiedBy(document)
		
	}

	def returnsTrueIfDocumentIsEarlierThanISOTime() {
		
		given: 'A timestamp and an oplog document'
			def timeStamp = '2012-12-26T15:19:40Z'
			def document = '{ts:{$ts:1356515303,$inc:1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
		
		when: 'timestamp criteria is applied to the document'
			TimestampCriteria criteria = new TimestampCriteria(timeStamp)
								
		then: 'it returns true if the document is older than the timestamp'
			criteria.isSatisfiedBy(document)
		
	}

	def returnsFalseIfDocumentIsLaterThanISOTime() {
		
		given: 'A timestamp and an oplog document'
			def timeStamp = '2012-12-26T15:19:40Z'
			def document = '{ts:{$ts:1357801207,$inc:1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
		
		when: 'timestamp criteria is applied to the document'
			TimestampCriteria criteria = new TimestampCriteria(timeStamp)
								
		then: 'it returns true if the document is older than the timestamp'
			!criteria.isSatisfiedBy(document)
	}

}
