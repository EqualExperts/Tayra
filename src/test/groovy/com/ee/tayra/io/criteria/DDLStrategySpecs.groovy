package com.ee.tayra.io.criteria


import spock.lang.Specification;

import static com.ee.tayra.io.criteria.DDLStrategy.*;
class DDLStrategySpecs extends Specification{
	static createCollection = '{ "create" : "countries", "capped" : null, "size" : null } }'
	static dropCollection = '{ "drop" : "countries"}}'
	static createIndex = '{ "_id" : { "$oid" : "511b232461ad583bb301e9ec"} , "ns" : "eelabs.countries" , "key" : { "name" : 1.0} , "name" : "name_1"}}'
	static dropIndex = '{ "deleteIndexes" : "countries" , "index" : { "name" : 1.0}}}'
	static invalidDocument = '{ "done" : "countries", "capped" : null, "size" : null } }'
	def document
	def strategy

	def producesCorrectStrategyInstance() {
		expect:
			strategy == DDLStrategy.create(document)
		where:
			document               | strategy
			createCollection       | CREATECOLLECTION
			dropCollection         | DROPCOLLECTION
			createIndex            | CREATEINDEX
			dropIndex              | DROPINDEX
			invalidDocument        | NO_STRATEGY
	}

	def shoutsWhenUnsupportedOperationIsSupplied() throws Exception{
		given :'a documents containg DDL Operation'
			def documentNamespace = 'eelabs.$cmd'
			def operation = new DDLOperation()
		when :
			def strategy = DDLStrategy.create(invalidDocument)
			strategy.extractCollection(invalidDocument)
		then :
			def problem =thrown(DDLOperationNotSupported)
			problem.message == 'Cannot recognize operation in the payload ' + invalidDocument
	}
}
