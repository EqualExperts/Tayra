package com.ee.tayra.io.reader.nio;

import java.io.IOException;
import java.util.Iterator;
import org.apache.log4j.helpers.LogLog;
import com.ee.tayra.io.reader.DocumentReader;
import com.ee.tayra.io.reader.ReadNotifier;

public class MemoryMappedDocumentReader implements DocumentReader {
  private static final int ONE_KB = 1024;
  private static final int DEFAULT_SIZE = 8 * ONE_KB;
  private final Chunker chunker;
  private Iterator<String> documentIterator;
  private Iterator<Chunk> chunkIterator;
  private ReadNotifier notifier;

  public MemoryMappedDocumentReader(final String fileName,
      final String memoryBufferSize) throws IOException {
    notifier = ReadNotifier.NONE;
    long bufferSize = getFileSize(memoryBufferSize);
    chunker = new Chunker(fileName, bufferSize);
    chunkIterator = chunker.iterator();
  }

  private long getFileSize(final String value) {
    if (value == null) {
      return DEFAULT_SIZE;
    }

    String s = value.trim().toUpperCase();
    long multiplier = 1;
    int index;

    if ((index = s.indexOf("KB")) != -1) {
      multiplier = ONE_KB;
      s = s.substring(0, index);
    } else if ((index = s.indexOf("MB")) != -1) {
      multiplier = ONE_KB * ONE_KB;
      s = s.substring(0, index);
    } else if ((index = s.indexOf("GB")) != -1) {
      multiplier = ONE_KB * ONE_KB * ONE_KB;
      s = s.substring(0, index);
    }
    if (s != null) {
      try {
        return Long.valueOf(s).longValue() * multiplier;
      } catch (NumberFormatException e) {
        LogLog.error("[" + s + "] is not in proper int form.");
        LogLog.error("[" + value + "] not in expected format.", e);
      }
    }
    return DEFAULT_SIZE;
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
