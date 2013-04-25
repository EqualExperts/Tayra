package com.ee.tayra.io.reader.nio

import com.ee.tayra.io.reader.nio.Chunker.ChunkIterator;
import com.ee.tayra.io.reader.nio.Chunk

import spock.lang.Specification;

public class ChunkerSpecs extends Specification {
	private static final String NEW_LINE = System.getProperty('line.separator')
	private final String document = "\"ts\""
	private Chunker chunker
	private Iterator<Chunk> chunkIterator
	private def file

	def setup() {
		file = File.createTempFile('test', 'out')
		file.withWriter { writer ->
			writer.write document
			writer.write NEW_LINE
		}
		chunker = new Chunker(file.absolutePath)
		chunkIterator = chunker.iterator()
	}

	def returnsAChunk () {
		when:'chunk is fetched'
			Chunk chunk = chunkIterator.next()

		then:'a chunk is obtained'
			chunk.getClass() == Chunk

		cleanup:
			chunker.close()
			file.delete()
	}

	def notifiesNextChunkIsPresent() {
		when:'chunk is looked for'
			boolean isNextChunkPresent = chunkIterator.hasNext()

		then:'chunk is found'
			isNextChunkPresent == true

		cleanup:
			chunker.close()
			file.delete()
	}

	def shoutsWhenChunkIsRemoved() {
		when:'chunk is removed'
			chunkIterator.remove()

		then:'error message should be thrown as'
			def problem = thrown(UnsupportedOperationException)
			problem.message == "remove chunk is not supported"
	}
}
