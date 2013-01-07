package com.ee.beaver.io

import spock.lang.Specification;

class SelectiveOplogReplayerSpecs extends Specification{
	private OplogReplayer mockTarget
	private Criteria selectCriteria
	private Replayer selectiveOplogReplayer
	private document = '{ "ts" : { "$ts" : 1357537752 , "$inc" : 1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	def setup() {
		mockTarget = Mock(OplogReplayer)
		selectCriteria = Stub(Criteria)
		selectiveOplogReplayer = new SelectiveOplogReplayer(selectCriteria, mockTarget)
	}
	def replaysDocumentIfCriteriaIsSatisfied() {
		given: 'Criteria is satisfied by document'
				selectCriteria.isSatisfiedBy(document) >> true
			
		when: 'Selective oplog replayer replays document'
				selectiveOplogReplayer.replayDocument(document)
				
		then: 'Document should be replayed'
				1 * mockTarget.replayDocument(document)
	}
	
	def replaysDocumentIfCriteriaIsNotSatisfied() {
		given: 'Criteria is not satisfied by document'
				selectCriteria.isSatisfiedBy(document) >> false
			
		when: 'Selective oplog replayer replays document'
				selectiveOplogReplayer.replayDocument(document)
				
		then:
				0 * mockTarget.replayDocument(document)
	}
}
