package com.ee.tayra.io.reader.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

public class Chunker implements Iterable<Chunk> {

  private static final long START_POSITION = 0L;
  private RandomAccessFile sourceFile;
  private File source = null;
  private static long fileSize;
  private static final PartialDocumentHandler partialDocumentHandler = new PartialDocumentHandler();

  public Chunker(String fileName) throws IOException {
    source = new File(fileName);
    sourceFile = new RandomAccessFile(source, "r");
    fileSize = sourceFile.length();
    setFilePointerTo(START_POSITION);
  }

  private final void setFilePointerTo(long newPosition) throws IOException {
    sourceFile.seek(newPosition);
  }

  @Override
  public Iterator<Chunk> iterator() {
    return new ChunkIterator(sourceFile);
  }

  public void close() throws IOException {
    sourceFile.close();
  }

  private static class ChunkIterator implements Iterator<Chunk> {
    private final RandomAccessFile sourceFile;
    private long filePointer;

    public ChunkIterator(RandomAccessFile sourceFile) {
      this.sourceFile = sourceFile;
    }

    @Override
    public boolean hasNext() {
      try {
        filePointer = sourceFile.getFilePointer();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return (filePointer < fileSize);
    }

    private final void setFilePointerTo(long newPosition)
        throws IOException {
      sourceFile.seek(newPosition);
    }

    @Override
    public Chunk next() {
      try {
        Chunk chunk = new Chunk(sourceFile.getChannel(), filePointer,
            fileSize, partialDocumentHandler);
        setFilePointerTo(filePointer + chunk.getReadSize());
        return chunk;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

  }

  static class PartialDocumentHandler {
    private String partialDoc = "";

    void handlePartialDocument(String partialDoc) {
      this.partialDoc = partialDoc;
    }

    String prependPartialDocumentTo(String document) {
      String completeDocument = partialDoc + document;
      partialDoc = "";
      return completeDocument;
    }
  }

}
