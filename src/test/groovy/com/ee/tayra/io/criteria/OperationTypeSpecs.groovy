package com.ee.tayra.io.criteria

import spock.lang.Specification

class OperationTypeSpecs extends Specification {
	
	
	def producesDDLOperation(){
		given:'a DDL operation'
			def documentNamespace = 'eelabs.$cmd'
		
		when: 
			def type = OperationType.create(documentNamespace);
		
		then:'type should be instance of DDLOperation'
			type.getClass() == DDLOperation
	}

	def producesDMLOperation() {
		given:'a DML operation'
			def documentNamespace = 'eelabs.countries'
		
		when:
			def type = OperationType.create(documentNamespace);
		
		then:'type should be instance of DMLOperation'
			type.getClass() == DMLOperation
	}
	
}

