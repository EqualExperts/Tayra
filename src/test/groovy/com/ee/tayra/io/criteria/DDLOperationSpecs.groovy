package com.ee.tayra.io.criteria

import spock.lang.Specification;

class DDLOperationSpecs extends Specification {
	
	def document = '''\
	{ "ts" : Timestamp(1360665338000, 1), "h" : NumberLong("7391500989066642719"),
	"v" : 2, "op" : "c", "ns" : "eelabs.$cmd",
	"o" : { "create" : "countries", "capped" : null, "size" : null } }
	''' 

	def invalidDocument = '''\
	{ "ts" : Timestamp(1360665338000, 1), "h" : NumberLong("7391500989066642719"),
	"v" : 2, "op" : "c", "ns" : "eelabs.$cmd",
	"o" : { "done" : "countries", "capped" : null, "size" : null } }
	''' 

	def static incomingNs ='eelabs'

	def matchesNamespaceForDDLOperations() {
		given :'a documents containg DDL Operation'
			def documentNamespace = 'eelabs.$cmd'	
			def operation = new DDLOperation()
		expect : 
			hasMatched == operation.match(document, documentNamespace, incomingNs)
		where :
			incomingNs         |   hasMatched
			'eelabs'           |   true
			'eelabs.countries' |   true
}

} 