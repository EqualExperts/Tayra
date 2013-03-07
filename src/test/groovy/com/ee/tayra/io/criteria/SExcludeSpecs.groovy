package com.ee.tayra.io.criteria

import spock.lang.Specification;

class SExcludeSpecs extends Specification {

	SExclude sExclude
	Criterion mockCriterion
	def document = '''{ts:{$ts:1357537752,$inc:1} , "h" : -2719432537158937612 ,
      "v" : 2 , "op" : "i" , "ns" : "test.things" ,
      "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'''

	def setup() {
		mockCriterion = Mock(Criterion)
		sExclude = new SExclude(mockCriterion)
	}

	def excludesDocumentIfItSatisfiesCriteria() {
		given: 'document satisfies criteria'
			mockCriterion.isSatisfiedBy(document) >> true
			
		when: 'sExclude is applied'
			boolean satisfied = sExclude.isSatisfiedBy(document)

		then: 'it excludes the document'
			! satisfied
	}

	def acceptsDocumentIfItDoesNotSatisfyCriteria() {
		given: 'document does not satisfy criteria'
			mockCriterion.isSatisfiedBy(document) >> false

		when: 'sExclude is applied'
			boolean satisfied = sExclude.isSatisfiedBy(document)

		then: 'it accepts the document'
			satisfied
	}
}
