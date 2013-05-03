package com.ee.tayra.io.reader.nio;

import java.io.IOException;

import com.ee.tayra.io.reader.DocumentReader;
import com.ee.tayra.io.reader.ReadNotifier;

public class MemoryMappedDocumentReader implements DocumentReader {

  private final Chunker chunker;
  private ReadNotifier notifier;

  public MemoryMappedDocumentReader(final String fileName,
      final long chunkSize) throws IOException {
    notifier = ReadNotifier.NONE;
    chunker = new Chunker(fileName, chunkSize);
  }

  @Override
  public final String readDocument() {
    notifier.notifyReadStart("");
    try {
    String document = chunker.getDocument();
    notifier.notifyReadSuccess(document);
    return document;
    } catch (Exception problem) {
      notifier.notifyReadFailure(null, problem);
    }
    return null;
  }

  @Override
  public final void close() throws IOException {
    chunker.close();
  }

}
