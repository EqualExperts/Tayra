package com.ee.tayra.io.criteria

import spock.lang.Specification;

class DMLOperationSpecs extends Specification {
	def document= '''
			"ns" : "eelabs.countries",
			"o" : { "_id" : ObjectId("511499dd9365898be4b00b0d"), "name" : "Test1" } }
			'''
def matchesNamespaceForDMLOperations() {
	given :'a documents containg DML Operation'
			def documentNamespace = 'eelabs.$cmd'	
			def incomingNs ='eelabs'
			def operation = new DMLOperation()
	when : 
	     	def hasMatched = operation.match(document, documentNamespace, incomingNs)
    then :
	        hasMatched
	  
}

}