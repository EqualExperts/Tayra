package com.ee.tayra.io.reader.nio

import java.io.Reader
import java.util.Iterator
import com.ee.tayra.io.reader.nio.Chunk
import com.ee.tayra.io.reader.nio.Chunker
import com.ee.tayra.io.reader.nio.Chunker.ChunkIterator
import com.ee.tayra.io.reader.nio.Chunk.DocumentIterator
import com.ee.tayra.io.reader.DocumentReader
import com.ee.tayra.io.reader.ReadNotifier
import spock.lang.Ignore
import spock.lang.Specification


class MemoryMappedDocumentReaderSpecs extends Specification {

	private ReadNotifier mockNotifier
	private String fileName = 'test.out'
	private DocumentReader fileDocumentReader
	private final String document = "\"ts\""
	private Chunker mockChunker = Stub(Chunker)
	private Chunk mockedChunk = Stub(Chunk)
	private Iterator<Chunk> mockChunkIterator = Stub(ChunkIterator)
	private Iterator<String> mockDocIterator = Stub(DocumentIterator)

	def setup() {
		mockNotifier = Mock(ReadNotifier)
		fileDocumentReader = new MemoryMappedDocumentReader(fileName)
		fileDocumentReader.notifier = mockNotifier
	}

	def notifiesBeforeStartingToReadADocument() {
		when: 'document is read'
			fileDocumentReader.readDocument()

		then: 'a notification of successful read is given'
			1 * mockNotifier.notifyReadStart("")
	}
}
