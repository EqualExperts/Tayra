package com.ee.tayra.utils

import spock.lang.Specification;
import static ByteUnit.*

class BytesFactorSpecs extends Specification {
	def ensuresDefaultValues() {
		given:'Data Units'
		  def unit = byteUnit
	
		expect:'default values are fetched'
		  unit.toInt() == intValue
	
		where:'default values to b'
		 byteUnit     |      intValue
		  NOTHING     |         1
			 B        |         1
			KB        |       1024
			MB        |    1024 * 1024
			GB        |  1024 * 1024 * 1024
	}
	
	def constructsFromStringRepresentation() {
		given:'Data Units'
		  def unit = from(stringValue)
	
		expect:'appropriate values are fetched'
		  unit == byteUnit
	
		where:'string values are...'
		  stringValue |  byteUnit  
			   'b'    |   B    
			  'kB'    |   KB   
			  'MB'    |   MB   
			  'GB'    |   GB
			  ''      |   B
			  'any'   |   B
	  }
}
