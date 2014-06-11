package com.ee.tayra.domain.operation

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import com.mongodb.util.JSONSerializers
import groovy.transform.TupleConstructor
import org.bson.types.BSONTimestamp

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
        return JSONSerializers.getStrict().serialize(objectStructure())
    }
    if(type == DBObject) {
      return objectStructure()
    }
    throw new IllegalArgumentException("Cannot convert to $type")
  }

}
