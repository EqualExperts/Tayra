package com.ee.tayra.io.reader

import spock.lang.Specification;

import com.ee.tayra.domain.Oplog
import com.ee.tayra.io.criteria.Criterion;
import com.ee.tayra.io.reader.CollectionReader;
import com.ee.tayra.io.reader.SelectiveOplogReader;

class SelectiveOplogReaderSpecs  extends Specification{
	private Criterion mockCriterion
	private SelectiveOplogReader selectiveOplogReader
	private Oplog mockOplog
	private CollectionReader mockOplogReader
	private boolean isTailable = false
	private String timestamp = null
	private document = '{ "ts" : { "$ts" : 1357537752 , "$inc" : 1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'

	def setup() {
		mockOplogReader = Mock(CollectionReader)
		mockCriterion = Mock(Criterion)
		mockOplog = Mock(Oplog)
		selectiveOplogReader = new SelectiveOplogReader(mockOplogReader, mockCriterion)
	}

	def returnsDocumentIfCriteriaIsSatisfied() {

		given: 'Criteria gets the criterion'
			mockCriterion.isSatisfiedBy(document) >> true
			mockOplogReader.readDocument() >> document

		when: 'Selective oplog reader reads document'
			def result =  selectiveOplogReader.readDocument()

		then: 'Document should be returned'
			result == document
	}

	def doesNotReplayDocumentIfCriteriaIsNotSatisfied() {

		given: 'Criteria gets the criterion'
			mockCriterion.isSatisfiedBy(document) >> false
			mockOplogReader.readDocument() >> null

		when: 'Selective oplog replayer replays document'
			def result = selectiveOplogReader.readDocument()

		then: 'Document should not be returned'
			result == ''
	}
}
