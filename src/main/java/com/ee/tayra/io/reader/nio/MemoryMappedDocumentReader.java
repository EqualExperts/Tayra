package com.ee.tayra.io.reader.nio;

import java.io.IOException;
import java.util.Iterator;

import com.ee.tayra.io.reader.DocumentReader;
import com.ee.tayra.io.reader.ReadNotifier;
import com.ee.tayra.utils.DataUnit;

public class MemoryMappedDocumentReader implements DocumentReader {

  private final Chunker chunker;
  private Iterator<String> documentIterator;
  private Iterator<Chunk> chunkIterator;
  private ReadNotifier notifier;

  public MemoryMappedDocumentReader(final String fileName,
      final String memoryBufferSize) throws IOException {
    notifier = ReadNotifier.NONE;
    DataUnit dataUnit = DataUnit.from(memoryBufferSize);
    long bufferSize = dataUnit.toLongValue();
    chunker = new Chunker(fileName, bufferSize);
    chunkIterator = chunker.iterator();
  }

  @Override
  public final String readDocument() {
    String document = null;
    notifier.notifyReadStart("");
    if (thereAreNoDocuments()) {
      if (hasMoreChunks()) {
        try {
          Chunk chunk = chunkIterator.next();
          documentIterator = chunk.iterator();
        } catch (Exception problem) {
          notifier.notifyReadFailure(document, problem);
        }
      } else { // no more documents
        return null;
      }
    }
    try {
      document = documentIterator.next();
      notifier.notifyReadSuccess(document);
    } catch (Exception problem) {
      notifier.notifyReadFailure(document, problem);
    }
    return document;
  }

  final boolean hasMoreChunks() {
    return chunkIterator.hasNext();
  }

  final boolean thereAreNoDocuments() {
    return (documentIterator == null || !documentIterator.hasNext());
  }

  @Override
  public final void close() throws IOException {
    chunker.close();
  }

}
