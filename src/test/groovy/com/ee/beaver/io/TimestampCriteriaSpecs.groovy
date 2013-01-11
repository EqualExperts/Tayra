package com.ee.beaver.io

import java.text.SimpleDateFormat

import spock.lang.Specification

class TimestampCriteriaSpecs extends Specification {

	def returnsTrueIfDocumentIsEarlierThanTimestamp () {
		
		given: 'A timestamp and an oplog document'
			def timeStamp = '-sUntil={ts:{$ts:1357537752,$inc:2}}'
			def document = '{ts:{$ts:1357537752,$inc:1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
		
		when: 'timestamp criteria is applied to the document'
			TimestampCriteria criteria = new TimestampCriteria(timeStamp)
								
		then: 'it returns true if the document is older than the timestamp'
			criteria.isSatisfiedBy(document)
		
	}
	
	def returnsFalseIfDocumentIsLaterThanTimestamp () {
		
		given: 'A timestamp and an oplog document'
			def timeStamp = '-sUntil={ts:{$ts:1300000000,$inc:1}}'
			def document = '{ts:{$ts:1357537752,$inc:1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
		
		when: 'timestamp criteria is applied to the document'
			TimestampCriteria criteria = new TimestampCriteria(timeStamp)
								
		then: 'it returns true if the document is older than the timestamp'
			! criteria.isSatisfiedBy(document)
		
	}
	
	def returnsUTCTime() {
		
		given:
			int timeStamp = 1357537752
		when:
			Date date = new Date(1356515380*1000L)
			SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd,HH:mm:ss")
			
		then:
			println format.parse('2013:01:07,11:19:12')
			println date
			println Long.MAX_VALUE
			
		
	}

}
