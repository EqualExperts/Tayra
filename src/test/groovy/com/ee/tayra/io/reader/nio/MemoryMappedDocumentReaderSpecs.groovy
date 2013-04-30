package com.ee.tayra.io.reader.nio

import spock.lang.Specification

import com.ee.tayra.io.reader.DocumentReader
import com.ee.tayra.io.reader.ReadNotifier
import com.ee.tayra.io.reader.nio.Chunk.DocumentIterator
import com.ee.tayra.io.reader.nio.Chunker.ChunkIterator

class MemoryMappedDocumentReaderSpecs extends Specification {
	private static final String NEW_LINE = System.getProperty('line.separator')
	private ReadNotifier mockNotifier
	private DocumentReader reader
	private final String document = "\"ts\""
	private Chunker mockChunker = Stub(Chunker)
	private Chunk mockedChunk = Stub(Chunk)
	private Iterator<Chunk> mockChunkIterator = Stub(ChunkIterator)
	private Iterator<String> mockDocIterator = Stub(DocumentIterator)
	private File file

	def setup() {
		mockNotifier = Mock(ReadNotifier)
		file = File.createTempFile('test', 'out')
		file.withWriter { writer ->
			writer.write document
			writer.write NEW_LINE
		}
		String bufferSize ='1KB'
		reader = new MemoryMappedDocumentReader(file.absolutePath, bufferSize)
		reader.notifier = mockNotifier
	}

	def cleanup() {
		reader.close()
		file.delete()
	}

	def readsADocument() {
		when: 'read is invoked'
			String document = reader.readDocument()

		then: 'the document is read'
			document == this.document
	}

	def notifiesBeforeStartingToReadADocument() {
		when: 'document is read'
			reader.readDocument()

		then: 'a notification of successful read is given'
			1 * mockNotifier.notifyReadStart("")
	}

	def shoutsWhenImproperBufferSizeIsSupplied() {
		given: 'an invalid String'
			String improperBufferSize = 'MB1'

		when: 'reader is initialized'
			reader = new MemoryMappedDocumentReader(file.absolutePath, improperBufferSize)
			reader.notifier = mockNotifier

		then: 'exception is thrown as'
			def problem = thrown(IllegalArgumentException)
			problem.message == "Don't know how to represent " + improperBufferSize
	}
}
