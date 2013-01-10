package com.ee.beaver.io

import spock.lang.Specification;

class SelectiveOplogReplayerSpecs extends Specification{
	private Replayer mockTarget
	private Criterion mockCriterion
	private Criteria mockCriteria
	private Replayer selectiveOplogReplayer
	private document = '{ "ts" : { "$ts" : 1357537752 , "$inc" : 1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	def setup() {
		mockTarget = Mock(OplogReplayer)
		mockCriteria = Stub(Criteria)
	}
	
	def replaysDocumentIfCriteriaIsSatisfied() {
				
		given: 'Criteria gets the criterion'
			mockCriteria.getCriterion () >> {
				mockCriterion = Mock(Criterion)
				mockCriterion.isSatisfiedBy(document) >> true
				mockCriterion
			}
			selectiveOplogReplayer = new SelectiveOplogReplayer(mockCriteria, mockTarget)
			
		when: 'Selective oplog replayer replays document'
				selectiveOplogReplayer.replay(document)
				
		then: 'Document should be replayed'
				1 * mockTarget.replay(document)
	}
	
	def doesNotReplayDocumentIfCriteriaIsNotSatisfied() {
		
		given: 'Criteria gets the criterion'
				mockCriteria.getCriterion () >> {
				mockCriterion = Mock(Criterion)
				mockCriterion.isSatisfiedBy(document) >> false
				mockCriterion
			}
			selectiveOplogReplayer = new SelectiveOplogReplayer(mockCriteria, mockTarget)
			
		when: 'Selective oplog replayer replays document'
				selectiveOplogReplayer.replay(document)
				
		then: 'Document should be replayed'
				0 * mockTarget.replay(document)
	}
	
}
