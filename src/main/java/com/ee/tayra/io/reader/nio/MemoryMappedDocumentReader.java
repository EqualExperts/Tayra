package com.ee.tayra.io.reader.nio;

import java.io.IOException;
import java.util.Iterator;

import com.ee.tayra.io.ReadNotifier;
import com.ee.tayra.io.reader.DocumentReader;

public class MemoryMappedDocumentReader implements DocumentReader {
  private ReadNotifier notifier;
  private Iterator<String> documentIterator;
  private Chunker chunker;
  private Iterator<Chunk> chunkIterator;

  public MemoryMappedDocumentReader(String fileName) throws IOException {
    notifier = ReadNotifier.NONE;
    chunker = new Chunker(fileName);
    chunkIterator = chunker.iterator();
  }

  @Override
  public String readDocument() {
    String document = null;
    notifier.notifyReadStart("");
    if (thereAreNoDocuments()) {
      if (chunkIterator.hasNext()) {
        try {
			Chunk chunk = chunkIterator.next();
			documentIterator = chunk.iterator();
		} catch (Exception problem) {
			notifier.notifyReadFailure(document, problem);
		}
      } else { //no more documents
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

boolean thereAreNoDocuments() {
	return documentIterator == null || (documentIterator.hasNext() == false);
}

  @Override
  public void close() throws IOException {
    chunker.close();
  }

}
