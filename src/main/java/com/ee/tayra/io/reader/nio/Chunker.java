package com.ee.tayra.io.reader.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

class Chunker implements Iterable<Chunk> {

  private static final long START_POSITION = 0L;
  private RandomAccessFile sourceFile;
  private File source = null;
  private static long fileSize;
  private static PartialDocumentHandler partialDocumentHandler =
      new PartialDocumentHandler();
  private final long chunkSize;

  public Chunker(final String fileName, final long bufferSize)
    throws IOException {
    source = new File(fileName);
    sourceFile = new RandomAccessFile(source, "r");
    this.chunkSize = bufferSize;
    fileSize = sourceFile.length();
    setFilePointerTo(START_POSITION);
  }

  private void setFilePointerTo(final long newPosition)
      throws IOException {
    sourceFile.seek(newPosition);
  }

  @Override
  public final Iterator<Chunk> iterator() {
    return new ChunkIterator(sourceFile, chunkSize);
  }

  public final void close() throws IOException {
    sourceFile.close();
  }

  private static class ChunkIterator implements Iterator<Chunk> {
    private final RandomAccessFile sourceFile;
    private long filePointer;
    private final long chunkSize;

    public ChunkIterator(final RandomAccessFile sourceFile,
      final long chunkSize) {
      this.sourceFile = sourceFile;
      this.chunkSize = chunkSize;
    }

    @Override
    public final boolean hasNext() {
      try {
        filePointer = sourceFile.getFilePointer();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return (filePointer < fileSize);
    }

    private void setFilePointerTo(final long newPosition)
        throws IOException {
      sourceFile.seek(newPosition);
    }

    @Override
    public final Chunk next() {
      try {
        Chunk chunk = new Chunk(sourceFile.getChannel(), filePointer,
            fileSize, chunkSize, partialDocumentHandler);
        setFilePointerTo(filePointer + chunk.getReadSize());
        return chunk;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public final void remove() {
      throw new UnsupportedOperationException("remove chunk is not supported");
    }

  }

  static class PartialDocumentHandler {
    private String partialDoc = "";

    final void handlePartialDocument(final String partialDoc) {
      this.partialDoc = partialDoc;
    }

    final String prependPartialDocumentTo(final String document) {
      String completeDocument = partialDoc + document;
      partialDoc = "";
      return completeDocument;
    }
  }

}
