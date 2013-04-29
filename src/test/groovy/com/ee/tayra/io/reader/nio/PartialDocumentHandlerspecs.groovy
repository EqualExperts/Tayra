package com.ee.tayra.io.reader.nio

import com.ee.tayra.io.reader.nio.Chunker.PartialDocumentHandler;
import com.sun.org.apache.bcel.internal.generic.NEW;

import spock.lang.Specification;

public class PartialDocumentHandlerspecs extends Specification{

  def completesPartialDocument() {
    given:'a partial document'
      String partialDocument = '{"ts":{ts:123'
      String incompleteDocument = ',inc:1}}'
      PartialDocumentHandler handler = new PartialDocumentHandler()

    and: 'it is passed to handler'
      handler.handlePartialDocument(partialDocument)

    when: 'incomplete document is injected to handler'
      String completeDoc = handler.prependPartialDocumentTo(incompleteDocument)

    then: 'complete document'
      completeDoc == '{"ts":{ts:123,inc:1}}'
  }
}
