package com.ee.tayra.domain.operation

import com.mongodb.DBObject
import groovy.json.JsonBuilder
import groovy.lang.Closure;
import groovy.transform.TupleConstructor;

import com.mongodb.util.JSON

@TupleConstructor
class DeleteDocumentBuilder extends DocumentBuilder {

	boolean b;

	Closure documentStructure() {
		def document = {
			ts JSON.serialize(ts)
			h h
			op op
			ns ns
			b b
			o JSON.serialize(o)
		}
	}
}
