package com.ee.tayra.io.criteria

import spock.lang.Specification

class OperationNsCriteriaSpecs extends Specification{
	static eelabsCountriesUpdateDoc = '{ "ts" : { "$ts" : 1360733107 , "$inc" : 1} , "h" : 5409713632279739576 , "v" : 2 , "op" : "u" , "ns" : "eelabs.countries","o2" : { "_id" : ObjectId("511499dd9365898be4b00b0d") },"o" : { "$set" : { "name" : "Test2" } } }'
	static eelabsCountriesInsertDoc = '{ "ts" : { "$ts" : 1360732964 , "$inc" : 1} , "h" : -3247792282971197891 , "v" : 2 , "op" : "i" , "ns" : "eelabs.countries" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	static eelabsCountriesDeleteDoc = '{ "ts" : { "$ts" : 1360685980 , "$inc" : 1} , "h" : -3549987509950246055 , "v" : 2 , "op" : "d" , "ns" : "eelabs.countries", "b" : true, "o" : { "_id" : ObjectId("51149b949365898be4b00b0e") } }'

	static eelabsPrefixedCollectionDoc = '{ "ts" : { "$ts" : 1360732964 , "$inc" : 1} , "h" : -3247792282971197891 , "v" : 2 , "op" : "i" , "ns" : "eelabs.people.address.street" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	
	static eelabsPeopleInsertDoc = '{ "ts" : { "$ts" : 1360732964 , "$inc" : 1} , "h" : -3247792282971197891 , "v" : 2 , "op" : "i" , "ns" : "eelabs.people" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'

	static eeCountriesInsertDoc = '{ "ts" : { "$ts" : 1360732964 , "$inc" : 1} , "h" : -3247792282971197891 , "v" : 2 , "op" : "i" , "ns" : "ee.countries" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'
	static eePeopleInsertDoc = '{ "ts" : { "$ts" : 1360685980 , "$inc" : 1} , "h" : -3549987509950246055 , "v" : 2 , "op" : "i" , "ns" : "ee.people" , "o" : { "_id" : { "$oid" : "50ea61d85bdcefd43e2994ae"} , "roll" : 0.0}}'

	def namespaceInsert ='eelabs.countries.insert'
	def namespaceUpdate ='eelabs.countries.update'
	def namespaceDelete ='eelabs.countries.remove'
	def namespaceWrongOperation ='eelabs.countries.wrong'
	def namespaceWithoutOperation ='eelabs.countries'
	def prefixedNamespaceInsert = 'eelabs.people.address.street.insert'

	def criteria
	def document

	def satisfiesNamespaceAndInsertOperationCriteria() {
		criteria = new NamespaceCriteria(namespaceInsert, false)
	expect:
		outcome == criteria.isSatisfiedBy(document)

	where:
		document                              | outcome
		eelabsCountriesInsertDoc              | true
		eelabsCountriesUpdateDoc              | false
		eelabsCountriesDeleteDoc              | false
	}

	def satisfiesNamespaceAndUpdateOperationCriteria() {
		criteria = new NamespaceCriteria(namespaceUpdate, false)
	expect:
		outcome == criteria.isSatisfiedBy(document)

	where:
		document                              | outcome
		eelabsCountriesInsertDoc              | false
		eelabsCountriesUpdateDoc              | true
		eelabsCountriesDeleteDoc              | false
	}

	def satisfiesNamespaceAndDeleteOperationCriteria() {
		criteria = new NamespaceCriteria(namespaceDelete, false)
	expect:
		outcome == criteria.isSatisfiedBy(document)

	where:
		document                              | outcome
		eelabsCountriesInsertDoc              | false
		eelabsCountriesUpdateDoc              | false
		eelabsCountriesDeleteDoc              | true
	}

	def satisfiesInsertOperationAndNotDatabaseCriteria() {
		criteria = new NamespaceCriteria(namespaceInsert, false)
	expect:
		outcome == criteria.isSatisfiedBy(document)

	where:
		document                              | outcome
		eeCountriesInsertDoc                  | false
		eelabsCountriesInsertDoc              | true
	}

	def satisfiesInsertOperationDatabaseAndNotCollectionCriteria() {
		criteria = new NamespaceCriteria(namespaceInsert, false)
	expect:
		outcome == criteria.isSatisfiedBy(document)

	where:
		document                              | outcome
		eelabsCountriesInsertDoc              | true
		eelabsPeopleInsertDoc                 | false
	}
	
	def satisfiesInsertOperationAndNotDatabaseAndCollectionCriteria() {
		criteria = new NamespaceCriteria(namespaceInsert, false)
	expect:
		outcome == criteria.isSatisfiedBy(document)

	where:
		document                              | outcome
		eePeopleInsertDoc                     | false
		eelabsCountriesInsertDoc              | true
	}
	
	def doesNotSatisfyWrongOperationCriteria() {
		criteria = new NamespaceCriteria(namespaceWrongOperation, false)
	expect:
		outcome == criteria.isSatisfiedBy(document)

	where:
		document                              | outcome
		eelabsCountriesInsertDoc              | false
	}
	
	def satisfiesNoOperationCriteria() {
		criteria = new NamespaceCriteria(namespaceWithoutOperation, false)
	expect:
		outcome == criteria.isSatisfiedBy(document)

	where:
		document                              | outcome
		eelabsCountriesInsertDoc              | true
	}

	def satisfiesPrefixedNamespaceAndOperationCriteria() {
		criteria = new NamespaceCriteria(prefixedNamespaceInsert, false)
	expect:
		outcome == criteria.isSatisfiedBy(document)

	where:
		document                               | outcome
		eelabsPrefixedCollectionDoc            | true
	}

	def shoutsWhenNoCollectionGivenWithOperation() {
		criteria = new NamespaceCriteria(prefixedNamespaceInsert, false)
	expect:
		outcome == criteria.isSatisfiedBy(document)

	where:
		document                               | outcome
		eelabsPrefixedCollectionDoc            | true
	}

//TODO	dbName.insert

}
