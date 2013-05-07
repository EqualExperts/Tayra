package com.ee.tayra.io.writer

import spock.lang.Specification

import com.ee.tayra.io.criteria.Criterion

class SelectiveOplogReplayerSpecs extends Specification{
  private Replayer mockTarget
  private Criterion mockCriterion
  private Replayer selectiveOplogReplayer
  private document = '{ "ts" : { "$ts" : 1357537752 , "$inc" : 1} , "h" : -2719432537158937612 , "v" : 2 , "op" : "i" , "ns" : "test.things" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'

  def setup() {
    mockTarget = Mock(OplogReplayer)
    mockCriterion = Mock(Criterion)
    selectiveOplogReplayer = new SelectiveOplogReplayer(mockCriterion, mockTarget)
  }

  def replaysDocumentIfCriteriaIsSatisfied() {

    given: 'Criteria gets the criterion'
      mockCriterion.isSatisfiedBy(document) >> true

    when: 'Selective oplog replayer replays document'
        selectiveOplogReplayer.replay(document)

    then: 'Document should be replayed'
        1 * mockTarget.replay(document)
  }

  def doesNotReplayDocumentIfCriteriaIsNotSatisfied() {

    given: 'Criteria gets the criterion'
      mockCriterion.isSatisfiedBy(document) >> false

    when: 'Selective oplog replayer replays document'
        selectiveOplogReplayer.replay(document)

    then: 'Document should be replayed'
        0 * mockTarget.replay(document)
  }

}
