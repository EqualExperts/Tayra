package com.ee.tayra.domain.operation

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DBObject
import groovy.transform.TupleConstructor

@TupleConstructor
class UpdateDocumentBuilder extends DocumentBuilder {

  DBObject o2

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
