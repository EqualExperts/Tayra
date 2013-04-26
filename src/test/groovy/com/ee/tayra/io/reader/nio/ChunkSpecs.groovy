package com.ee.tayra.io.reader.nio

import java.util.Iterator;

import com.ee.tayra.io.reader.nio.Chunker.PartialDocumentHandler;

import spock.lang.Specification;

class ChunkSpecs extends Specification{

	private static final String NEW_LINE = System.getProperty('line.separator')

	private static final int ONE_KB = 1024
	private final String document = '{"{\"ts\""}}'
	private final long chunkSize =  ONE_KB
	private Chunk chunk
	private def handler
	private Iterator<String> documentIterator
	private def file

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

	def notifiesWhenADocumentIsAvaliable() {
		when:'a document is looked for'
			boolean isDocumentPresent = documentIterator.hasNext()

		then:'a document is found'
			isDocumentPresent == true

		cleanup:
			file.delete()
	}

	def readsADocument() {
		when:''
			String document = documentIterator.next()

		then:''
			document == this.document

		cleanup:
			file.delete()
	}

	def shoutsWhenDocumentIsRemoved() {
		when:'document is removed'
			documentIterator.remove()

		then:'error message should be thrown as'
			def problem = thrown(UnsupportedOperationException)
			problem.message == "remove not supported"
	}

}
