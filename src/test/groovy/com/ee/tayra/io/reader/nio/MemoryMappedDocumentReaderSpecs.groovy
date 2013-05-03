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
		long bufferSize = 1024
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

	def worksProperlyWhenDocumentFlowsAcrossChunks() {
		given:''
			def actualDocument = '{ "ts" : { "$ts" : 1360000000, "$inc" : 1} , "h" : 2763120522771994968 , "v" : 2 , "op" : "i" , "ns" : "tayra.performance" , "o" : { "_id" : { "$oid" : "0014c08045661688b4dbc81e"} , "name" : "*********1"}}'
			file = File.createTempFile('test', 'out')
			file.withWriter { writer ->
				writer.write actualDocument
				writer.write NEW_LINE
			}
			long bufferSize = 8
			reader = new MemoryMappedDocumentReader(file.absolutePath, bufferSize)
			reader.notifier = mockNotifier

		when:''
			String expectedDocument;
			expectedDocument = reader.readDocument()
			println actualDocument
			println expectedDocument

		then:''
			expectedDocument == actualDocument

	}

}
