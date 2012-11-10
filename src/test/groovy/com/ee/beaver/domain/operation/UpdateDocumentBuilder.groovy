package com.ee.beaver.domain.operation

import com.mongodb.DBObject
import groovy.json.JsonBuilder
import groovy.transform.TupleConstructor;

import com.mongodb.util.JSON

@TupleConstructor
class UpdateDocumentBuilder extends DocumentBuilder {

	DBObject o2
	
	def asType(Class type) {
		if(type == String) {
			def builder = new JsonBuilder()
			builder {
				ts JSON.serialize(ts)
				h h
				op op
				ns ns
				o2 JSON.serialize(o2)
				o JSON.serialize(o)
			}
			return builder.toString()
		}
		if(type == DBObject) {
			return (DBObject) JSON.parse(this as String)
		}
		throw new IllegalArgumentException("Cannot convert to $type")
	}
}
