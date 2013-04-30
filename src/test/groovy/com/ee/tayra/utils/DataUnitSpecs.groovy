package com.ee.tayra.utils

import spock.lang.Specification
import static DataUnit.*

class DataUnitSpecs extends Specification{

	def ensuresDefaultValues() {
		given:'Data Units'
			def unit = dataUnit

		expect:'default values are fetched'
			unit.value() == expectedValue
			unit.bytesFactor() == expectedByteFactor
			unit.toLongValue() == expectedlongValue

		where:'default values to b'
		   dataUnit |  expectedValue   | expectedByteFactor |  expectedlongValue
			   B    |         1        |         1          |         1
			  KB    |         1        |       1024         |       1024
			  MB    |         1        |    1024 * 1024     |    1024 * 1024
			  GB    |         1        | 1024 * 1024 * 1024 | 1024 * 1024 * 1024
	}

	def shoutsWhenEmptyStringIsPassed() {
		when:'an empty string is passed'
			from('')

		then:'error message should be thrown as'
			def problem = thrown(IllegalArgumentException)
			problem.message == "Valid values are B, KB, MB, GB"
	}

	def shoutsWhenNullIsPassed() {
		when:'null is pased'
			from(null)

		then:'error message should be thrown as'
			def problem = thrown(IllegalArgumentException)
			problem.message == "Valid values are B, KB, MB, GB"
	}


	def convertsToLong() {
		when:'a value is passed'
			def unit = from('5MB')

		then:'equivalent long value is obtained'
		    unit.toLongValue() == 5 * 1024 * 1024
	}

	def returnsAppropriateDataUnit() {
		given:'Data Units'
			def unit = from(stringValue)

		expect:'default values are fetched'
			unit.value() == value
			unit.bytesFactor() == byteFactor
			unit.toLongValue() == longValue

		where:'default values to b'
			stringValue |  value  |     byteFactor     |     longValue
			     '9B'   |    9    |         1          |         9
			    '8KB'   |    8    |       1024         |       8 * 1024
			    '7MB'   |    7    |    1024 * 1024     |    7 * 1024 * 1024
			    '6GB'   |    6    | 1024 * 1024 * 1024 | 6 * 1024 * 1024 * 1024
	}
}
