package com.ee.tayra.domain.operation

import groovy.transform.TupleConstructor

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
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
	
	def objectStructure() {
		BasicDBObjectBuilder
			.start()
				.add('ts', ts)
				.add('h', h)
				.add('op', op)
				.add('ns', ns)
				.add('o2', o2)
				.add('o', o)
			.get()
	}
}
