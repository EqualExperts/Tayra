package com.ee.tayra.utils

import static com.ee.tayra.utils.DataUnit.*
import spock.lang.Specification

class DataUnitSpecs extends Specification {

  def shoutsWhenEmptyStringIsPassed() {
    when:'an empty string is passed'
      from('')

    then:'error message should be thrown as'
      def problem = thrown(IllegalArgumentException)
      problem.message == "Valid values are B, KB, MB, GB"
  }

  def shoutsWhenNullIsPassed() {
    when:'null is passed'
      from(null)

    then:'error message should be thrown as'
      def problem = thrown(IllegalArgumentException)
      problem.message == "Valid values are B, KB, MB, GB"
  }


  def convertsToBytes() {
    given:'Data Units'
      def unit = from(stringValue)

    expect:'appropriate values are fetched'
      unit.value() == value
      unit.toBytes() == bytes

    where:'default values to b'
      stringValue |  value  |    bytes
           '9B'   |    9    |       9
          '8KB'   |    8    |     8 * 1024
          '7MB'   |    7    |   7 * 1024 * 1024
          '6GB'   |    6    | 6 * 1024 * 1024 * 1024
  }

  def areEqual() {
    given:'Data Units'
      def unitOne = from(valueOne)
      def unitTwo = from(valueTwo)

    expect:'default values are fetched'
      unitOne.equals(unitTwo) == outcome

    where:'default values to b'
      valueOne | valueTwo | outcome
      '9B'   |    '9B'  |  true
      '7MB'  |    '7GB' |  false
  }

  def areEqualUsingOperator() {
    given:'Data Units'
      def unitOne = from(valueOne)
      def unitTwo = from(valueTwo)

    expect:'default values are fetched'
      outcome == (unitOne == unitTwo)

    where:'default values to b'
      valueOne | valueTwo | outcome
        '9B'   |   '9B'   |  true
        '7MB'  |   '7GB'  |  false
  }

  def shoutsWhenImproperBufferSizeIsSupplied() {
    given: 'invalid buffer size'
      def invalidBufferSize = 'MB1'

    when: 'invalid buffer size is passed'
      from(invalidBufferSize)

    then:'error message should be thrown as'
      def problem = thrown(IllegalArgumentException)
      problem.message == "Don't know how to represent " + invalidBufferSize
  }

  def shoutsWhenUnitIsMissing() {
    given: 'invalid buffer size'
      def invalidBufferSize = '8'

    when: 'invalid buffer size is passed'
      from(invalidBufferSize)

    then:'error message should be thrown as'
      def problem = thrown(IllegalArgumentException)
      problem.message == "Don't know how to represent " + invalidBufferSize
  }
}
