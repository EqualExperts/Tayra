package com.ee.tayra.domain.operation

import com.mongodb.DBObject
import groovy.json.JsonBuilder
import groovy.transform.TupleConstructor;

import com.mongodb.util.JSON

@TupleConstructor
class UpdateDocumentBuilder extends DocumentBuilder {

	DBObject o2

	Closure documentStructure() {
		def document = {
			ts JSON.serialize(ts)
			h h
			op op
			ns ns
			o2 JSON.serialize(o2)
			o JSON.serialize(o)
		}
	}
}
