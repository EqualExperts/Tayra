package com.ee.tayra.io.reader.nio

import com.ee.tayra.io.reader.nio.Chunker.PartialDocumentHandler;
import com.sun.org.apache.bcel.internal.generic.NEW;

import spock.lang.Specification;

public class PartialDocumentHandlerspecs extends Specification{

  private PartialDocumentHandler handler
  static partialDocumentOne = '{ "ts" : { "$ts" : 1367215856 , "$inc" : 1} , "h" : 2577419153919943492 , "v" : 2 , "op" : "u" , "ns" : "Tayra.people" , "o2" : { "_id" : "joe"} , "o" : { "_id" : "joe" , "name" : "Joe Bookreader" , "addresses" : [ { "street" : "{{123 Fake Street}}"'
  static partialDocumentTwo = '{ "ts" : { "$ts" : 1367215856 , "$inc" : 1} , "h" : 2577419153919943492 , "v" : 2 , "op" : "u" , "ns" : "Tayra.people" , "o2" : { "_id" : "joe"} , "o" : { "_id" : "joe" , "name" : "Joe Bookreader" , "addresses" : [ { "street" : "123 Fake Street}}"'
  static partialDocumentThree = '{ "ts" : { "$ts" : 1367215856 , "$inc" : 1} , "h" : 2577419153919943492 , "v" : 2 , "op" : "u" , "ns" : "Tayra.people" , "o2" : { "_id" : "joe"} , "o" : { "_id" : "joe" , "name" : "Joe Bookreader" , "addresses" : [ { "street" : "{{123 Fake Street'
  static partialDocumentFour = '{ "ts" : { "$ts" : 1367215856 , "$inc" : 1} , "h" : 2577419153919943492 , "v" : 2 , "op" : "u" , "ns" : "Tayra.people" , "o2" : { "_id" : "joe"} , '

  def setup() {
    handler = new PartialDocumentHandler()
  }

  def completesPartialDocument() {
    given:'a partial document'
      String partialDocument = '{"ts":{ts:123'
      String incompleteDocument = ',inc:1}}'

    and: 'it is passed to handler'
      handler.handlePartialDocument(partialDocument)

    when: 'incomplete document is injected to handler'
      String completeDoc = handler.prependPartialDocumentTo(incompleteDocument)

    then: 'complete document'
      completeDoc == '{"ts":{ts:123,inc:1}}'
  }


  def notifiesWhenADocumentIsPartial() {
    expect: 'partial documents are identified'
      isPartial == handler.isPartial(document)

    where:
      document                       | isPartial
      partialDocumentOne             | true
      partialDocumentTwo             | true
      partialDocumentThree           | true
      partialDocumentFour            | true
  }

}
