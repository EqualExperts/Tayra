package com.ee.tayra.domain.operation

import groovy.json.JsonBuilder
import groovy.transform.*

import org.bson.types.BSONTimestamp

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import com.mongodb.util.JSON
import com.mongodb.util.JSONSerializers

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

  Closure documentStructure() {
    def document = {
      ts JSONSerializers.getStrict().serialize(ts)
      h h
      op op
      ns ns
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
        .add('o', o)
      .get()
  }

  def asType(Class type) {
    if(type == String) {
      def builder = new JsonBuilder()
      builder documentStructure()
      return builder.toString()
    }
    if(type == DBObject) {
      return objectStructure()
    }
    throw new IllegalArgumentException("Cannot convert to $type")
  }

}
