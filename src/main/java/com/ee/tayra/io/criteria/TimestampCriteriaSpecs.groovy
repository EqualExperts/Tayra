package com.ee.tayra.io.criteria
import static TimestampCriteria.*;
import spock.lang.Specification
class TimestampCriteriaSpecs extends Specification {

	static def sinceTime = 'Since'
	static def untilTime = 'Until'
	def producesAppropriateTimestampCriteria () {
				def timestamp = '{ts:{$ts:1357537752,$inc:2}}'
		expect :
				klass == TimestampCriteria.create(timeLimit,timestamp).getClass()
		where :
				timeLimit   |   klass
				sinceTime   |   TimestampCriteria.Since
				untilTime   |   TimestampCriteria.Until
		}
}
