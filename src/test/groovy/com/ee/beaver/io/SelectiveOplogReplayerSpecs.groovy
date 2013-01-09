package com.ee.beaver.io

import spock.lang.Specification;

class SelectiveOplogReplayerSpecs extends Specification{
	private Replayer mockTarget
	private Criteria criteria
	private Replayer selectiveOplogReplayer
	private document = '{ "ts" : { "$ts" : 1357537752 , "$inc" : 1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	def setup() {
		mockTarget = Mock(OplogReplayer)
		criteria = Stub(Criteria)
		selectiveOplogReplayer = new SelectiveOplogReplayer(criteria, mockTarget)
	}
	def replaysDocumentIfCriteriaIsSatisfied() {
		given: 'Criteria is satisfied by document'
				criteria.isSatisfiedBy(document) >> true
			
		when: 'Selective oplog replayer replays document'
				selectiveOplogReplayer.replay(document)
				
		then: 'Document should be replayed'
				1 * mockTarget.replay(document)
	}
	
	def replaysDocumentIfCriteriaIsNotGiven() {
		given: 'Criteria is satisfied by document'
				criteria.notGiven() >> true
			
		when: 'Selective oplog replayer replays document'
				selectiveOplogReplayer.replay(document)
				
		then: 'Document should be replayed'
				1 * mockTarget.replay(document)
	}
	
	def replaysDocumentIfCriteriaIsNotSatisfied() {
		given: 'Criteria is not satisfied by document'
				criteria.isSatisfiedBy(document) >> false
			
		when: 'Selective oplog replayer replays document'
				selectiveOplogReplayer.replay(document)
				
		then:
				0 * mockTarget.replay(document)
	}
}
