package com.ee.tayra.io.reader.nio

import java.util.Iterator;

import com.ee.tayra.io.reader.nio.Chunker.PartialDocumentHandler;

import spock.lang.Specification;

class ChunkSpecs extends Specification{

	private static final String NEW_LINE = System.getProperty('line.separator')
	private static String tempDirectory = System.getProperty("java.io.tmpdir")
	private static final int ONE_KB = 1024
	private final String document = '{{ts:1}}'
	private final long chunkSize =  ONE_KB
	private Chunk chunk
	private def handler
	private Iterator<String> documentIterator
	private File file

	static partialDocumentOne = '{ "ts" : { "$ts" : 1367215856 , "$inc" : 1} , "h" : 2577419153919943492 , "v" : 2 , "op" : "u" , "ns" : "Tayra.people" , "o2" : { "_id" : "joe"} , "o" : { "_id" : "joe" , "name" : "Joe Bookreader" , "addresses" : [ { "street" : "{{123 Fake Street}}"'
	static partialDocumentTwo = '{ "ts" : { "$ts" : 1367215856 , "$inc" : 1} , "h" : 2577419153919943492 , "v" : 2 , "op" : "u" , "ns" : "Tayra.people" , "o2" : { "_id" : "joe"} , "o" : { "_id" : "joe" , "name" : "Joe Bookreader" , "addresses" : [ { "street" : "123 Fake Street}}"'
	static partialDocumentThree = '{ "ts" : { "$ts" : 1367215856 , "$inc" : 1} , "h" : 2577419153919943492 , "v" : 2 , "op" : "u" , "ns" : "Tayra.people" , "o2" : { "_id" : "joe"} , "o" : { "_id" : "joe" , "name" : "Joe Bookreader" , "addresses" : [ { "street" : "{{123 Fake Street'
	static partialDocumentFour = '{ "ts" : { "$ts" : 1367215856 , "$inc" : 1} , "h" : 2577419153919943492 , "v" : 2 , "op" : "u" , "ns" : "Tayra.people" , "o2" : { "_id" : "joe"} , '


	def setup() {
		file = File.createTempFile('test', 'out')
		file.withWriter { writer ->
			writer.write document
			writer.write NEW_LINE
		}
		handler = new PartialDocumentHandler()
		RandomAccessFile mappedFile = new RandomAccessFile(file, "r")
		chunk = new Chunk(mappedFile.getChannel(), mappedFile.getFilePointer(),
			mappedFile.length(), chunkSize, handler)
		documentIterator = chunk.iterator()
	}

	def cleanup() {
		file.delete()
	}

	def notifiesWhenADocumentIsAvaliable() {
		when:'a document is looked for'
			boolean isDocumentPresent = documentIterator.hasNext()

		then:'a document is found'
			isDocumentPresent == true
	}

	def readsADocument() {
		when:''
			String document = documentIterator.next()

		then:''
			document == this.document
	}

	def shoutsWhenDocumentIsRemoved() {
		when:'document is removed'
			documentIterator.remove()

		then:'error message should be thrown as'
			def problem = thrown(UnsupportedOperationException)
			problem.message == "remove not supported"
	}

	def notifiesWhenADocumentIsPartial() {
		expect: 'partial documents are identified'
			isPartial == documentIterator.isPartial(document)

		where:
			document                       | isPartial
			partialDocumentOne             | true
			partialDocumentTwo             | true
			partialDocumentThree           | true
			partialDocumentFour            | true
	}
}
