package com.ee.tayra.domain.operation

import com.mongodb.BasicDBObjectBuilder
import groovy.transform.TupleConstructor

@TupleConstructor
class DeleteDocumentBuilder extends DocumentBuilder {

  boolean b;

  def objectStructure() {
    BasicDBObjectBuilder
      .start()
        .add('ts', ts)
        .add('h', h)
        .add('op', op)
        .add('ns', ns)
        .add('b', b)
        .add('o', o)
      .get()
    }
}
