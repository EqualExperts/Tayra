package com.ee.tayra.io.reader.nio

import spock.lang.Specification

public class ChunkerSpecs extends Specification {
  private static final String NEW_LINE = System.getProperty('line.separator')
  private final String document = "\"ts\""
  private static final int ONE_KB = 1024
  private final long chunkSize = ONE_KB
  private Chunker chunker
  private Iterator<Chunk> chunkIterator
  private File file

  def setup() {
    file = File.createTempFile('test', 'out')
    file.withWriter { writer ->
      writer.write document
      writer.write NEW_LINE
    }
    chunker = new Chunker(file.absolutePath,chunkSize)
    chunkIterator = chunker.iterator()
  }

  def cleanup() {
    chunker.close()
    file.delete()
  }

  def returnsAChunk () {
    when:'chunk is fetched'
      Chunk chunk = chunkIterator.next()

    then:'a chunk is obtained'
      chunk.getClass() == Chunk
  }

  def notifiesNextChunkIsPresent() {
    when:'chunk is looked for'
      boolean isNextChunkPresent = chunkIterator.hasNext()

    then:'chunk is found'
      isNextChunkPresent == true
  }

  def returnsOnlyOneChunkForFileSizeLessThanChunkSize() {
    given:'no chunk is read'
      int chunksRead = 0

    when:'chunk is looked for'
      while(chunkIterator.hasNext()) {
        Chunk chunk = chunkIterator.next()
        chunksRead++
      }
      boolean isNextChunkPresent = chunkIterator.hasNext()

    then:'only One chunk is created'
      chunksRead == 1
  }

  def shoutsWhenChunkIsRemoved() {
    when:'chunk is removed'
      chunkIterator.remove()

    then:'error message should be thrown as'
      def problem = thrown(UnsupportedOperationException)
      problem.message == "remove chunk is not supported"
  }
}
