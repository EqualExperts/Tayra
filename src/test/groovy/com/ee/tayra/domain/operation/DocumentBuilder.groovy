package com.ee.tayra.domain.operation

import groovy.json.JsonBuilder
import groovy.transform.*

import org.bson.types.BSONTimestamp
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import com.mongodb.util.JSON

@TupleConstructor
class DocumentBuilder {
	
	BSONTimestamp ts
	String h
	String op
	String ns
	DBObject o
	
	String toString() {
		asType String
	}
	
	def asType(Class type) {
		if(type == String) {
			def builder = new JsonBuilder() 
			builder {
				ts JSON.serialize(ts)
				h h
				op op
				ns ns
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
